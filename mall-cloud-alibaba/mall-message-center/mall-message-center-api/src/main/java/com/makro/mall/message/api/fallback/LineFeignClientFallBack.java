package com.makro.mall.message.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.message.api.LineFeignClient;
import com.makro.mall.message.dto.LineMessagePageFeignDTO;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/5/7
 */
@Component
@Slf4j
public class LineFeignClientFallBack implements LineFeignClient {

    @Override
    public BaseResponse<Boolean> broadcast(String message) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse multicastFlex(LineSendMessage lineSendMessage) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<LineMessagePageFeignDTO> linePage(MessagePageReqVO vo) {
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
