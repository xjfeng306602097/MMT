package com.makro.mall.admin.controller.client;

import com.makro.mall.admin.service.MmStoreService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaojunfeng
 * @description MM Store
 * @date 2021/11/9
 */
@Api(tags = "MM Store接口")
@RestController
@RequestMapping("/api/v1/stores/client")
@RequiredArgsConstructor
public class MmStoreClient {

    private final MmStoreService mmStoreService;


    @ApiOperation(value = "刷新store")
    @GetMapping("/syncFromMakro")
    public BaseResponse<Boolean> syncFromMakro() {
        mmStoreService.syncFromMakro();
        return BaseResponse.success(true);
    }

}
