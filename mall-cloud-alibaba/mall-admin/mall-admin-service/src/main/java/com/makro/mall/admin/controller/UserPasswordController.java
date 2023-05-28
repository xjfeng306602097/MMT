package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.nacos.common.utils.UuidUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.makro.mall.admin.common.constant.EmailConstants;
import com.makro.mall.admin.pojo.dto.ResetPasswordDTO;
import com.makro.mall.admin.pojo.entity.SysUser;
import com.makro.mall.admin.service.SysUserService;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.util.PasswordUtil;
import com.makro.mall.message.api.MailMessageFeignClient;
import com.makro.mall.message.enums.MailTypeEnum;
import com.makro.mall.message.pojo.entity.MailMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/10
 */
@Api(tags = "用户接口")
@RestController
@RequestMapping("/api/v1/password")
@Slf4j
@RequiredArgsConstructor
public class UserPasswordController implements EmailConstants {

    private final SysUserService sysUserService;

    private final MailMessageFeignClient mailMessageFeignClient;

    private final RedisTemplate redisTemplate;

    @Value("${password.reset.url:https://dev-jc.makrogo.com/makroDigital/login/reset}")
    private String resetUrl;

    @ApiOperation(value = "重置密码")
    @GetMapping("/reset/default")
    public BaseResponse defaultReset(@RequestParam String username) {
        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        Assert.isTrue(user != null, StatusCode.USER_NOT_EXIST);
        Assert.isTrue(user.getEmail() != null, StatusCode.USER_EMAIL_EMPTY);
        // 重置密码
        String password = PasswordUtil.randomPW(8);
        if (sysUserService.resetPassword(password, user)) {
            MailMessage mailMessage = new MailMessage();
            mailMessage.setSuccessCount(0);
            mailMessage.setDelay(30L);
            mailMessage.setSubject("Reset Your password");
            mailMessage.setToUser(new String[]{user.getEmail()});
            mailMessage.setContent(String.format("Dear %s, your password has been reset: %s, please check out",
                    user.getNickname(), password));
            mailMessage.setMailTypeEnum(MailTypeEnum.TEXT);
            mailMessageFeignClient.add(mailMessage);
            return BaseResponse.success();
        }
        return BaseResponse.error();
    }

    @ApiOperation(value = "修改密码")
    @PostMapping("/reset")
    public BaseResponse reset(@RequestBody ResetPasswordDTO dto) {
        // 检查token是否存在并匹配对应的username
        String cacheValue = (String) redisTemplate.opsForValue().get(RedisConstants.RESET_PASSWORD_TOKEN + dto.getToken());
        if (StrUtil.isNotEmpty(cacheValue) && StrUtil.isNotEmpty(dto.getUsername()) && dto.getUsername().equals(cacheValue)) {
            SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, dto.getUsername()));
            Assert.isTrue(user != null, StatusCode.USER_NOT_EXIST);
            // 重置密码
            if (sysUserService.resetPassword(dto.getPassword(), user)) {
                MailMessage mailMessage = new MailMessage();
                mailMessage.setSuccessCount(0);
                mailMessage.setDelay(30L);
                mailMessage.setSubject("Reset Your password finished");
                mailMessage.setToUser(new String[]{user.getEmail()});
                MailMessage.H5MailInfo mailInfo = new MailMessage.H5MailInfo();
                mailInfo.setTemplateContent(HTML_PASSWORD_TEMPLATE);
                mailInfo.setParams(Lists.newArrayList(user.getUsername(),
                        "Your password has been reset, please check out",
                        "<Strong> Your password is:" + dto.getPassword() + "</Strong>",
                        String.valueOf(new Date())));
                mailMessage.setH5MailInfo(mailInfo);
                mailMessage.setMailTypeEnum(MailTypeEnum.H5_TEMPLATE);
                mailMessageFeignClient.add(mailMessage);
                return BaseResponse.success();
            }
        }
        return BaseResponse.error(StatusCode.PASSWORD_RESET_URL_EXPIRE);
    }

    @ApiOperation(value = "忘记密码")
    @GetMapping("/forget")
    public BaseResponse forget(@RequestParam String username) {
        SysUser user = sysUserService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        Assert.isTrue(user != null, StatusCode.USER_NOT_EXIST);
        Assert.isTrue(user.getEmail() != null, StatusCode.USER_EMAIL_EMPTY);
        String uuid = UuidUtils.generateUuid();
        MailMessage mailMessage = new MailMessage();
        mailMessage.setSuccessCount(0);
        mailMessage.setDelay(30L);
        mailMessage.setSubject("Reset Your password from Makro Mail");
        mailMessage.setToUser(new String[]{user.getEmail()});
        MailMessage.H5MailInfo mailInfo = new MailMessage.H5MailInfo();
        mailInfo.setTemplateContent(HTML_PASSWORD_TEMPLATE);
        mailInfo.setParams(Lists.newArrayList(user.getUsername(), "Please click the below link to reset your password, this link will expire in 3 hours",
                String.format("<a href='%s'><Strong>Reset Now</Strong></a>", (resetUrl + "?username=" + username + "&token=" + uuid))
                , String.valueOf(new Date())));
        mailMessage.setH5MailInfo(mailInfo);
        mailMessage.setMailTypeEnum(MailTypeEnum.H5_TEMPLATE);
        mailMessageFeignClient.add(mailMessage);
        redisTemplate.opsForValue().set(RedisConstants.RESET_PASSWORD_TOKEN + uuid, username, RedisConstants.RESET_PASSWORD_TIME, TimeUnit.SECONDS);
        return BaseResponse.success();
    }


}
