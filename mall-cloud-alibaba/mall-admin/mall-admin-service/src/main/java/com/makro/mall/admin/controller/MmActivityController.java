package com.makro.mall.admin.controller;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.dto.MmActivityPageRepDTO;
import com.makro.mall.admin.pojo.dto.MmActivityPageReqDTO;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmBounceRate;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.entity.MmDetail;
import com.makro.mall.admin.pojo.vo.MmActivityBatchReqVO;
import com.makro.mall.admin.pojo.vo.MmActivityVO;
import com.makro.mall.admin.pojo.vo.MmDetailVO;
import com.makro.mall.admin.pojo.vo.RollBackReqVO;
import com.makro.mall.admin.service.MmActivityService;
import com.makro.mall.admin.service.MmBounceRateService;
import com.makro.mall.admin.service.MmDetailService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.product.pojo.vo.ProductVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description MM活动
 * @date 2021/10/18
 */
@Api(tags = "MM活动接口")
@RestController
@RequestMapping("/api/v1/activity")
@RequiredArgsConstructor
public class MmActivityController {

    private final MmActivityService mmActivityService;

    private final MmDetailService mmDetailService;

    private final MmBounceRateService mmBounceRateService;

    @ApiOperation(value = "(列表分页)")
    @PostMapping("/page")
    public BaseResponse<IPage<MmActivityPageRepDTO>> page(@RequestBody MmActivityPageReqDTO dto) {
        return BaseResponse.success(mmActivityService.getPage(dto));
    }

    @ApiOperation(value = "活动详情")
    @GetMapping("/{id}")
    public BaseResponse<MmActivityVO> detail(@ApiParam(value = "活动id", required = true) @PathVariable Integer id) {
        return BaseResponse.success(mmActivityService.detail(id));
    }

    @ApiOperation(value = "活动详情-根据code获取")
    @GetMapping("/mmCode/{mmCode}")
    public BaseResponse<MmActivityVO> getByCode(@PathVariable String mmCode) {
        return BaseResponse.success(mmActivityService.getByCode(mmCode));
    }

    @ApiOperation(value = "活动列表-批量根据code获取")
    @GetMapping("/mmCodes/{mmCodes}")
    public BaseResponse<IPage<MmActivity>> getByCodes(@PathVariable JSONArray mmCodes,
                                                      @ApiParam(value = "页码", required = true) Long page,
                                                      @ApiParam(value = "每页数量", required = true) Long limit) {
        return BaseResponse.success(mmActivityService.getBaseMapper().selectPage(new MakroPage<>(page, limit), new LambdaQueryWrapper<MmActivity>()
                .in(MmActivity::getMmCode, mmCodes)));
    }

    @ApiOperation(value = "新增活动")
    @PostMapping
    public BaseResponse<MmActivity> add(@Validated @RequestBody MmActivity activity) {
        mmActivityService.add(activity);
        return BaseResponse.success(activity);
    }


    @ApiOperation(value = "新增活动,带上商品数据")
    @PostMapping("/saveWithProduct")
    public BaseResponse<Boolean> saveWithProduct(@Validated @RequestBody MmActivityBatchReqVO vo) {
        return BaseResponse.success(mmActivityService.saveWithProduct(vo));
    }

    @ApiOperation(value = "修改活动")
    @PutMapping(value = "/{id}")
    public BaseResponse<Boolean> update(
            @PathVariable Long id,
            @Validated @RequestBody MmActivity activity) {
        activity.setId(id);
        boolean isSuccess = mmActivityService.updateById(activity.filterDuplicate());
        // 保存BounceRate
        if (isSuccess) {
            MmActivity dbActivity = mmActivityService.getById(id);
            MmBounceRate rate = new MmBounceRate();
            rate.setMmCode(dbActivity.getMmCode());
            rate.setThreshold(activity.getBounceRate());
            mmBounceRateService.saveOrUpdate(rate, new LambdaQueryWrapper<MmBounceRate>()
                    .eq(MmBounceRate::getMmCode, dbActivity.getMmCode()));
        }
        return BaseResponse.judge(isSuccess);
    }

    @ApiOperation(value = "修改活动-by MMCode")
    @PutMapping(value = "/code/{code}")
    public BaseResponse<Boolean> updateCode(
            @PathVariable String code,
            @Validated @RequestBody MmActivity activity) {
        activity.setMmCode(code);
        return BaseResponse.judge(mmActivityService.update(activity, new LambdaQueryWrapper<MmActivity>().eq(MmActivity::getMmCode, code)));
    }

    @ApiOperation(value = "清除模板-by MMCode")
    @PutMapping(value = "/clear-template")
    public BaseResponse<Boolean> clearTemplate(List<String> code) {
        return BaseResponse.judge(mmActivityService.update(new LambdaUpdateWrapper<MmActivity>()
                .in(MmActivity::getMmCode, code).set(MmActivity::getMmTemplateCode, null)));
    }

    @ApiOperation(value = "删除活动")
    @DeleteMapping("/{ids}")
    public BaseResponse<Boolean> delete(@ApiParam(value = "id集合", required = true) @PathVariable String ids) {
        Boolean status = mmActivityService.delete(ids);
        return BaseResponse.judge(status);
    }

    @ApiOperation(value = "查询mmDetails相关信息")
    @GetMapping("/mmCode/{mmCode}/mm-details")
    public BaseResponse<List<MmDetailVO<ProductVO>>> getMmDetails(@PathVariable String mmCode,
                                                                  Long pageSort,
                                                                  Long sort) {
        return BaseResponse.success(mmDetailService.listRelatedInfosByCode(mmCode, pageSort, sort));
    }

    @ApiOperation(value = "插入mmDetails数据")
    @PostMapping("/mmCode/{mmCode}/mm-details")
    public BaseResponse<Boolean> saveMmDetails(@PathVariable String mmCode,
                                               @RequestBody List<MmDetail> details) {
        return BaseResponse.success(mmDetailService.batchSave(mmCode, details));
    }

    @ApiOperation(value = "回滚MM状态")
    @PutMapping("/rollback/{mmCode}")
    public BaseResponse<Boolean> rollBack(@PathVariable String mmCode,
                                          @RequestBody RollBackReqVO vo) {
        return BaseResponse.judge(mmActivityService.rollBack(mmCode, vo.getStatus()));
    }

    @ApiOperation(value = "获取MM客户列表,根据MM的segment查找客户")
    @GetMapping("/getCustomer/{mmCode}")
    public BaseResponse<List<MmCustomer>> getPublishUsers(@PathVariable String mmCode,
                                                          @ApiParam(value = "传入 lineId/email phone必有 传别的都查全部", required = true) String filter) {
        return BaseResponse.success(mmActivityService.getPublishUsers(mmCode, filter));
    }

    @ApiOperation(value = "清理app列表缓存数据")
    @GetMapping("/cleanUpCache")
    public BaseResponse<Boolean> cleanUpCache() {
        mmActivityService.cleanUpCache();
        return BaseResponse.success();
    }


    @ApiOperation(value = "活动结束定时调度", hidden = true)
    @GetMapping("/scanMmActivityForFailure")
    public BaseResponse<Boolean> scanMmActivityForFailure() {
        return BaseResponse.success(mmActivityService.scanMmActivityForFailure());
    }

}
