package com.makro.mall.stat.controller;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.component.ShortLinkComponent;
import com.makro.mall.stat.pojo.dto.ShortLinkGenerateDTO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Resource;
import java.net.URI;

/**
 * @author xiaojunfeng
 * @description 短链接口
 * @date 2022/8/1
 */
@RestController
@RequestMapping("/api/v1/short-link")
@Api(tags = "短链接口")
public class ShortLinkController {

    @Resource
    private ShortLinkComponent shortLinkComponent;

    @PostMapping
    public BaseResponse<String> shortLink(@RequestBody ShortLinkGenerateDTO dto) {
        URI uri = UriComponentsBuilder.fromUriString(dto.getOriginUrl()).queryParams(dto.getParams()).build().toUri();
        return BaseResponse.success(shortLinkComponent.shortLink(uri));
    }

    @DeleteMapping
    public BaseResponse delShortLink(@RequestBody String shortLinkCode) {
        shortLinkComponent.delShortLink(shortLinkCode);
        return BaseResponse.success();
    }

}
