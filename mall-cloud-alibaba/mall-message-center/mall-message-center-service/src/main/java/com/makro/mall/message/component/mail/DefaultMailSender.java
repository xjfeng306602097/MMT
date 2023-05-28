package com.makro.mall.message.component.mail;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.message.pojo.dto.H5MailMessageDTO;
import com.makro.mall.message.pojo.dto.MailMessageDTO;
import com.makro.mall.message.pojo.dto.SendMailResultDTO;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.service.MailMessageService;
import com.makro.mall.message.service.MessagePropertiesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xiaojunfeng
 * @description 邮件发送器
 * @date 2021/11/3
 */
@Component
@Slf4j
public class DefaultMailSender implements MailSenderChain {

    @Autowired
    @Qualifier(value = "mailExecutor")
    private Executor mailExecutor;

    @Autowired
    private MailMessageService mailMessageService;

    @Autowired
    private MessagePropertiesService messagePropertiesService;

    public static Integer countStep(Integer size, int limt) {
        return (size + limt - 1) / limt;
    }

    @Override
    public boolean accept(MailMessageDTO dto) {
        return StrUtil.equals(dto.getSender(), messagePropertiesService.findFirstById("1").getMailApp());
    }

    /**
     * 调用链路：
     * 1.先创建发送邮件的请求到MQ(创建邮件发送),MQ收到消息后,将数据落地到mongodb
     * 2.根据发起的请求的发送类型判断,每条消息有对应的延时时间，如果是0，则入库后立马发送消息到MQ(发送邮件)
     * 如果不是,则延时发送
     * 3.收到MQ消息过来后，执行发送命令，发送成功/失败，直接修改mongodb数据的状态
     *
     * @param dto
     */
    @Override
    public void send(MailMessageDTO dto) {
        // MQ消息过来
        MakroMailSender sender = messagePropertiesService.getSenderByEmailAddress();
        String content;
        AtomicInteger successCount = new AtomicInteger();
        boolean isHtml = false;
        try {
            switch (dto.getMailTypeEnum()) {
                case H5:
                    content = buildH5(dto);
                    isHtml = true;
                    break;
                case H5_TEMPLATE:
                    content = buildH5ByTemplate(dto);
                    isHtml = true;
                    break;
                case TEXT:
                default:
                    content = dto.getContent();
                    break;
            }
            // 判断是否需要进行分片,超过limit的情况下，需要进行分片执行
            List<SendMailResultDTO> messages = new ArrayList<>();
            if (dto.getToUser().length > sender.getLimit()) {
                // 切割List,拆分成多个List，用线程池进行处理
                ArrayList<List<String>> lists = new ArrayList<>();
                Stream.iterate(0, n -> n + 1).limit(countStep(dto.getToUser().length, sender.getLimit())).
                        forEach(i -> lists.add(Stream.of(dto.getToUser()).skip((long) i * sender.getLimit()).limit(sender.getLimit())
                                .collect(Collectors.toList())));
                String finalContent = content;
                boolean finalIsHtml = isHtml;
                messages = lists.stream().map(item -> {
                    SendMailResultDTO<MimeMessage> sendMailDTO = SendMailResultDTO.init(dto.getId(), dto.getToUser());
                    try {
                        processMessage(dto, sender.getJavaMailSender(), finalContent, sendMailDTO, finalIsHtml);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                    return sendMailDTO;
                }).collect(Collectors.toList());
            } else {
                SendMailResultDTO<MimeMessage> sendMailDTO = SendMailResultDTO.init(dto.getId(), dto.getToUser());
                processMessage(dto, sender.getJavaMailSender(), content, sendMailDTO, isHtml);
                messages.add(sendMailDTO);
            }
            CompletableFuture<SendMailResultDTO>[] futures = new CompletableFuture[messages.size()];
            for (int i = 0; i < messages.size(); i++) {
                SendMailResultDTO sendMailDTO = messages.get(i);
                futures[i] = CompletableFuture.supplyAsync(() -> {
                    log.info("开始发送邮件,任务ID{},收件人{}", sendMailDTO.getId(), sendMailDTO.getToUser());
                    sender.getJavaMailSender().send((MimeMessage) sendMailDTO.getMail());
                    log.info("结束发送邮件,任务ID{},收件人{}", sendMailDTO.getId(), sendMailDTO.getToUser());
                    return sendMailDTO.success(sendMailDTO.getToUser().length);
                }, mailExecutor).thenApplyAsync(result -> {
                    // 发送成功，计数器统计
                    successCount.addAndGet(result.getSuccessCount());
                    return result;
                }).exceptionally(e -> {
                    // TODO 分片异常,将重发数据到MQ进行二次重发
                    log.error("发送邮件异常,请求体{}", sendMailDTO, e);
                    return sendMailDTO.fail(e.getMessage());
                });
            }
            // 阻塞主线程不现实
            CompletableFuture.allOf(futures).whenCompleteAsync((r, e) -> {
                // 整体完成后，更新发送状态
                MailMessage status = updateSendLogStatus(dto, successCount);
                //如果为任务发布则更新用户状态
                mailMessageService.updateTaskLogStatus(dto.getMmPublishJobEmailTaskId(), status.getStatus(), dto.getToUser());
                //如果为任务发布则更新任务状态
                mailMessageService.updateTaskStatus(dto.getMmPublishJobEmailTaskId(), status.getStatus(), dto.getToUser());
            }, mailExecutor);
        } catch (Exception e) {
            // 发送失败,异常处理,修改消息状态
            log.error("DefaultMailSender.send发送失败", e);
            MailMessage mailMessage = new MailMessage();
            mailMessage.setId(dto.getId());
            mailMessage.setStatus(MessageSendEnum.FAILED);
            mailMessageService.update(mailMessage);

            mailMessageService.updateTaskStatus(dto.getMmPublishJobEmailTaskId(), MessageSendEnum.FAILED, dto.getToUser());
        }
    }

    private MailMessage updateSendLogStatus(MailMessageDTO dto, AtomicInteger successCount) {
        log.info("发送邮件{}成功,更新数据库", dto.getId());
        MailMessage mailMessage = new MailMessage();
        mailMessage.setId(dto.getId());
        mailMessage.setSuccessCount(successCount.get());
        MessageSendEnum status = successCount.get() == 0 ? MessageSendEnum.FAILED : (successCount.get() == dto.getToUser().length ? MessageSendEnum.SUCCEEDED : MessageSendEnum.PARTIALLY_SUCCEEDED);
        mailMessage.setStatus(status);
        mailMessageService.update(mailMessage);
        return mailMessage;
    }


    private void processMessage(MailMessageDTO dto, JavaMailSenderImpl sender, String content,
                                SendMailResultDTO<MimeMessage> sendMailDTO, boolean isHtml) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(Stream.of(dto.getToUser()).filter(StrUtil::isNotEmpty).toArray(String[]::new));
        if (StrUtil.isNotEmpty(dto.getToCc())) {
            helper.setCc(dto.getToCc());
        }
        helper.setFrom(Objects.requireNonNull(sender.getUsername()));
        helper.setSubject(dto.getSubject());
        helper.setText(content, isHtml);
        sendMailDTO.setMail(message);
    }

    private String buildH5(MailMessageDTO dto) throws IOException {
        return getH5Content((H5MailMessageDTO) dto);
    }

    private String buildH5ByTemplate(MailMessageDTO dto) throws IOException {
        List<String> params = ((H5MailMessageDTO) dto).getParams();
        return MessageFormat.format(getH5Content((H5MailMessageDTO) dto), params.toArray());
    }

    private String getH5Content(H5MailMessageDTO dto) throws IOException {
        if (StrUtil.isNotEmpty(dto.getTemplateContent())) {
            return dto.getTemplateContent();
        }
        String path = dto.getPath();
        URL url1 = new URL(path);
        URLConnection uc = url1.openConnection();
        InputStream inputStream = uc.getInputStream();
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.error("读取文件失败，fileName:{}", path, e);
            return null;
        } finally {
            inputStream.close();
            fileReader.close();
        }
        return buffer.toString();
    }

}
