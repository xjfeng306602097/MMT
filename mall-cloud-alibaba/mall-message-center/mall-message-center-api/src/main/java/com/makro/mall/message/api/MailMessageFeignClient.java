package com.makro.mall.message.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.api.fallback.MailMessageFeignClientFallBack;
import com.makro.mall.message.dto.MailMessagePageFeignDTO;
import com.makro.mall.message.dto.MailSubscriptionFeignDTO;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.MailSubscription;
import com.makro.mall.message.vo.MailMessagePageReqVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description ums接口
 * @date 2022/3/10
 */
@FeignClient(value = "makro-message", fallback = MailMessageFeignClientFallBack.class)
public interface MailMessageFeignClient {

    @PostMapping("/api/v1/mail")
    BaseResponse add(@RequestBody MailMessage mailMessage);

    @PostMapping("/api/v1/mail/list")
    BaseResponse<MailMessagePageFeignDTO> list(@RequestBody MailMessagePageReqVO vo);

    @PostMapping("/api/v1/mail/subList")
    BaseResponse<List<MailSubscription>> subList(@RequestBody MailSubscriptionFeignDTO dto);

}
