package com.makro.mall.admin.api.fallback;

import com.makro.mall.admin.api.PublishFeignClient;
import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.pojo.dto.MmPublishJobTaskMonitorDTO;
import com.makro.mall.admin.pojo.dto.UserTaskMonitorDTO;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.stat.pojo.snapshot.PageTotalSuccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class PublishFeignFallbackClient implements PublishFeignClient {

    @Override
    public BaseResponse<Boolean> scanMmPublishJobTask() {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse scanMmPublishJobLineTask() {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<Integer> getMmPublishTotal(String mmCode, String channel) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse<List<AssemblyDataByMemberTypeDTO>> getMmPublishTotalGroupByMemberType(Set<String> mmCode) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse updateState(MmPublishJobTaskMonitorDTO dto) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse updateUserState(UserTaskMonitorDTO dto) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }

    @Override
    public BaseResponse scanMmPublishJobSmsTask() {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }


    @Override
    public BaseResponse<List<PageTotalSuccess>> pageTotalSuccess(Date time) {
        log.error(StatusCode.SERVICE_DEGRADE.getMsg());
        return BaseResponse.error(StatusCode.SERVICE_DEGRADE);
    }
}
