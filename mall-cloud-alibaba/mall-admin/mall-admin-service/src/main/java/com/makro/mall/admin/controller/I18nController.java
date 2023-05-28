package com.makro.mall.admin.controller;

import com.makro.mall.common.enums.I18nEnum;
import com.makro.mall.common.model.BaseResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description i18n接口
 * @date 2021/11/16
 */
@Api(tags = "i18n接口")
@RestController
@RequestMapping("/api/v1/i18n")
@RequiredArgsConstructor
public class I18nController {

    @ApiOperation(value = "列出所有多语言")
    @GetMapping
    public BaseResponse<List<String>> list() {
        return BaseResponse.success(I18nEnum.getAllValues());
    }

}
