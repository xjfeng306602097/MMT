package com.makro.mall.message.controller.client;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.pojo.entity.MessageProperties;
import com.makro.mall.message.service.MessagePropertiesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Api(tags = "消息配置接口")
@RestController
@RequestMapping("/api/v1/properties/client")
@RequiredArgsConstructor
public class MessagePropertiesClientController {

    private final MessagePropertiesService messagePropertiesService;

    @ApiOperation(value = "详情", hidden = true)
    @GetMapping("/{id}")
    public BaseResponse<MessageProperties> detail(@PathVariable String id) {
        return BaseResponse.success(messagePropertiesService.findFirstById(id));
    }

}
