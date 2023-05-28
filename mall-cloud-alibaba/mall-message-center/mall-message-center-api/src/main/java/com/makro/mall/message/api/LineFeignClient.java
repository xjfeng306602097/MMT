package com.makro.mall.message.api;


import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.api.fallback.LineFeignClientFallBack;
import com.makro.mall.message.dto.LineMessagePageFeignDTO;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.message.vo.MessagePageReqVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author xiaojunfeng
 * @description ums接口
 * @date 2022/3/10
 */
@FeignClient(value = "makro-message", fallback = LineFeignClientFallBack.class, contextId = "line-client")
public interface LineFeignClient {

    @PostMapping("/api/v1/line/broadcast")
    BaseResponse<Boolean> broadcast(@RequestBody String message);

    @PostMapping("/api/v1/line/multicastFlex")
    BaseResponse<Boolean> multicastFlex(@RequestBody LineSendMessage lineSendMessage);

    @PostMapping("/api/v1/line/page")
    BaseResponse<LineMessagePageFeignDTO> linePage(@RequestBody MessagePageReqVO vo);

}
