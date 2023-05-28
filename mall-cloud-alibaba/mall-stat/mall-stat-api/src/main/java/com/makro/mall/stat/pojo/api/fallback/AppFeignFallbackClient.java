package com.makro.mall.stat.pojo.api.fallback;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.stat.pojo.api.AppFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component
@Slf4j
public class AppFeignFallbackClient implements AppFeignClient {

    @Override
    public BaseResponse<Set<String>> getTotalByTime(Date startTime, Date endTime) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
