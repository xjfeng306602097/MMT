package com.makro.mall.stat.pojo.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.stat.pojo.api.ShortLinkFeignClient;
import com.makro.mall.stat.pojo.dto.ShortLinkGenerateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ShortLinkFeignFallbackClient implements ShortLinkFeignClient {

    @Override
    public BaseResponse<String> shortLink(ShortLinkGenerateDTO dto) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse delShortLink(String shortLinkCode) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
