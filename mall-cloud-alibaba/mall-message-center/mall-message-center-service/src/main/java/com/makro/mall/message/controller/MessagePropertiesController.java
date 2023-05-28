package com.makro.mall.message.controller;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.message.pojo.entity.MessageProperties;
import com.makro.mall.message.service.MessagePropertiesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Api(tags = "消息配置接口")
@RestController
@RequestMapping("/api/v1/properties")
@RequiredArgsConstructor
public class MessagePropertiesController {

    private final MessagePropertiesService messagePropertiesService;

    @ApiOperation(value = "详情")
    @GetMapping
    public BaseResponse<MessageProperties> detail() {
        return BaseResponse.success(messagePropertiesService.findFirstById("1"));
    }

    @ApiOperation(value = "新增消息配置")
    @PostMapping
    public BaseResponse<MessageProperties> add(@RequestBody MessageProperties messageProperties) {
        return BaseResponse.success(messagePropertiesService.save(messageProperties));
    }

    @ApiOperation(value = "修改消息配置")
    @PutMapping()
    public BaseResponse<MessageProperties> update( @RequestBody MessageProperties messageProperties) {
        messageProperties.setId("1");
        messageProperties.setLastUpdater(JwtUtils.getUsername());
        return BaseResponse.success(messagePropertiesService.update(messageProperties));
    }


}
