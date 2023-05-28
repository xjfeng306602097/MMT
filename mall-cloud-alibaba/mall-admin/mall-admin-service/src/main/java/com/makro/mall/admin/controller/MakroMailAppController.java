package com.makro.mall.admin.controller;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/18
 */

import com.makro.mall.admin.pojo.dto.MakroMailPageReq;
import com.makro.mall.admin.pojo.vo.MmActivityAppPageRepVO;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * @author xiaojunfeng
 * @description MM活动
 * @date 2021/10/18
 */
@Api(tags = "MakroMail App接口")
@RestController
@RequestMapping("/app-api/v1/mm")
@RequiredArgsConstructor
@Slf4j
public class MakroMailAppController {

    private final MmActivityService mmActivityService;

    @ApiOperation(value = "(列表分页)")
    @PostMapping("/page")
    public BaseResponse<MakroPage<MmActivityAppPageRepVO>> page(@RequestBody SortPageRequest<MakroMailPageReq> request) throws ExecutionException {
        return BaseResponse.success(mmActivityService.appPageFromCache(request));
    }

}
