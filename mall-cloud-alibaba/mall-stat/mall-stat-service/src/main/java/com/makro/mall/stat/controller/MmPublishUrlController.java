package com.makro.mall.stat.controller;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.component.MmPublishUrlCache;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Line中间页接口")
@RestController
@RequestMapping("/api/v1/line/getMailUrl")
@RequiredArgsConstructor
public class MmPublishUrlController {

    private final MmPublishUrlCache mmPublishUrlCache;

    @GetMapping
    @ApiOperation("获取mm中间页(缓存30天)")
    public BaseResponse<String> getMailUrl(@ApiParam("lineUserId") String sub,
                                           @ApiParam("mmCode-mm") String mmCode,
                                           @ApiParam("pageNo-p") String pageNo) {
        return BaseResponse.success(mmPublishUrlCache.getMailUrl(sub, mmCode, pageNo));
    }

    @DeleteMapping
    @ApiOperation("删除mm中间页缓存")
    public BaseResponse<Boolean> delMailUrl(@ApiParam("lineUserId") String sub,
                                            @ApiParam("mmCode-mm") String mmCode,
                                            @ApiParam("pageNo-p") String pageNo) {
        return BaseResponse.success(mmPublishUrlCache.delMailUrl(sub, mmCode, pageNo));
    }
}
