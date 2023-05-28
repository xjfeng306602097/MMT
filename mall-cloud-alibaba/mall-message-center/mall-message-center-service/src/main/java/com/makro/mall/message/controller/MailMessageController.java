package com.makro.mall.message.controller;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.message.dto.MailMessagePageFeignDTO;
import com.makro.mall.message.dto.MailSubscriptionFeignDTO;
import com.makro.mall.message.pojo.dto.SubscribeDTO;
import com.makro.mall.message.pojo.dto.UnsubscribeLogPageReq;
import com.makro.mall.message.pojo.dto.UnsubscribePageReq;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.MailSubscription;
import com.makro.mall.message.service.MailMessageService;
import com.makro.mall.message.vo.MailMessagePageReqVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 邮件控制器
 * @date 2021/11/4
 */
@Api(tags = "邮件相关接口")
@RestController
@RequestMapping("/api/v1/mail")
@RequiredArgsConstructor
public class MailMessageController {

    private final MailMessageService mailMessageService;

    @Value("${mail.unsubscribe.salt:makro123}")
    private String salt;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<Page<MailMessage>> pageList(@RequestBody MailMessagePageReqVO vo) {
        String status = vo.getStatus().getStatus();
        Integer page = vo.getPage();
        Integer size = vo.getSize();
        Integer isDelete = vo.getIsDelete();
        String mmPublishJobEmailTaskId = vo.getMmPublishJobEmailTaskId();
        String[] toUser = vo.getToUser();
        return BaseResponse.success(mailMessageService.page(status, page, size, isDelete, mmPublishJobEmailTaskId, toUser));
    }

    @ApiOperation(value = "Feign所用", hidden = true)
    @PostMapping("/list")
    public BaseResponse<MailMessagePageFeignDTO> list(@RequestBody MailMessagePageReqVO vo) {
        String status = vo.getStatus().getStatus();
        Integer page = vo.getPage();
        Integer size = vo.getSize();
        Integer isDelete = vo.getIsDelete();
        String mmPublishJobEmailTaskId = vo.getMmPublishJobEmailTaskId();
        String[] toUser = vo.getToUser();
        Page<MailMessage> page1 = mailMessageService.page(status, page, size, isDelete, mmPublishJobEmailTaskId, toUser);
        return BaseResponse.success(new MailMessagePageFeignDTO(page1.getTotalElements(), page1.getContent()));
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<MailMessage> detail(@PathVariable String id) {
        return BaseResponse.success(mailMessageService.findFirstById(id));
    }

    @ApiOperation(value = "新增邮件")
    @PostMapping
    public BaseResponse add(@RequestBody MailMessage mailMessage) {
        return BaseResponse.success(mailMessageService.save(mailMessage));
    }

    @ApiOperation(value = "修改邮件")
    @PutMapping(value = "/{id}")
    public BaseResponse update(@PathVariable String id, @RequestBody MailMessage mailMessage) {
        mailMessage.setId(id);
        return BaseResponse.success(mailMessageService.update(mailMessage));
    }

    @ApiOperation(value = "删除邮件,状态由0改为1")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("id集合,用,隔开") @PathVariable String ids) {
        return BaseResponse.success(mailMessageService.removeByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList())));
    }

    @ApiOperation(value = "取消订阅")
    @PostMapping("/unsub")
    public BaseResponse unsubscribe(@Valid @RequestBody SubscribeDTO dto) {
        if (dto.checkAddress(salt)) {
            mailMessageService.unSubscribe(dto);
            return BaseResponse.success();
        }
        return BaseResponse.error(StatusCode.ILLEGAL_STATE);
    }

    @ApiOperation(value = "订阅")
    @PostMapping("/sub")
    public BaseResponse subscribe(@Valid @RequestBody SubscribeDTO dto) {
        if (dto.checkAddress(salt)) {
            mailMessageService.subScribe(dto);
            return BaseResponse.success();
        }
        return BaseResponse.error(StatusCode.ILLEGAL_STATE);
    }

    @ApiOperation(value = "取消订阅日志查询")
    @PostMapping("/unsubLog")
    public BaseResponse unsubscribeLog(@RequestBody UnsubscribeLogPageReq req) {
        return BaseResponse.success(mailMessageService.unSubscribeLog(req));
    }

    @ApiOperation(value = "取消订阅查询")
    @PostMapping("/unsubList")
    public BaseResponse unsubscribeList(@RequestBody UnsubscribePageReq req) {
        return BaseResponse.success(mailMessageService.unSubscribeList(req));
    }

    @ApiOperation(value = "取消订阅查询")
    @PostMapping("/subList")
    public BaseResponse<List<MailSubscription>> subList(@RequestBody MailSubscriptionFeignDTO dto) {
        return BaseResponse.success(mailMessageService.listMailSubscriptionsByAddressAndStatus(dto.getAddresses(), dto.getStatus()));
    }

}
