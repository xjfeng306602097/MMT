package com.makro.mall.admin.controller;


import com.makro.mall.admin.pojo.entity.SysOauthClient;
import com.makro.mall.admin.service.SysOauthClientService;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaojunfeng
 * @description 客户端API接口
 * @date 2021/10/13
 */
@Api(tags = "客户端接口")
@RestController
@RequestMapping("/api/v1/oauth-clients")
@Slf4j
@RequiredArgsConstructor
public class OauthClientController {

    private final SysOauthClientService sysOauthClientService;

    @ApiOperation(value = "客户端详情")
    @GetMapping("/{clientId}")
    public BaseResponse<SysOauthClient> detail(@ApiParam("客户端id") @PathVariable String clientId) {
        SysOauthClient client = sysOauthClientService.getById(clientId);
        return BaseResponse.success(client);
    }

}
