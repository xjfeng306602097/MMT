package com.makro.mall.message.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.message.api.MailMessageFeignClient;
import com.makro.mall.message.dto.MailMessagePageFeignDTO;
import com.makro.mall.message.dto.MailSubscriptionFeignDTO;
import com.makro.mall.message.pojo.entity.MailMessage;
import com.makro.mall.message.pojo.entity.MailSubscription;
import com.makro.mall.message.vo.MailMessagePageReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/5/7
 */
@Component
@Slf4j
public class MailMessageFeignClientFallBack implements MailMessageFeignClient {
    @Override
    public BaseResponse add(MailMessage mailMessage) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }


    @Override
    public BaseResponse<MailMessagePageFeignDTO> list(MailMessagePageReqVO vo) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<List<MailSubscription>> subList(MailSubscriptionFeignDTO dto) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }


}
