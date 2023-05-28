package com.makro.mall.template.controller;

import cn.hutool.core.util.ObjectUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.template.pojo.dto.MmPublishReq;
import com.makro.mall.template.pojo.dto.MmTemplatePageDTO;
import com.makro.mall.template.pojo.entity.MmPublishRecord;
import com.makro.mall.template.pojo.entity.MmTemplate;
import com.makro.mall.template.pojo.entity.MmTemplateDraft;
import com.makro.mall.template.pojo.entity.MmUserCache;
import com.makro.mall.template.pojo.vo.MmTemplateVO;
import com.makro.mall.template.service.MMTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 组件控制器
 * @date 2021/10/27
 */
@Api(tags = "模板相关接口")
@RestController
@RequestMapping("/api/v1/template")
@RequiredArgsConstructor
@Slf4j
public class MmTemplateController {

    private static final Integer SHOULD_FILTER = 1;
    private final MMTemplateService mmTemplateService;


    @ApiOperation(value = "回滚模板状态")
    @PutMapping("/rollback/{code}")
    public BaseResponse<Boolean> rollBack(@PathVariable String code) {
        return BaseResponse.judge(mmTemplateService.rollBack(code));
    }

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public BaseResponse<Page<MmTemplatePageDTO>> pageList(@ApiParam("页码") Integer page,
                                                          @ApiParam("每页数量") Integer limit,
                                                          @ApiParam("起始时间") Date begin,
                                                          @ApiParam("终止时间") Date end,
                                                          @ApiParam("状态") String status,
                                                          @ApiParam("名称") String name,
                                                          @ApiParam("mmCode") String mmCode,
                                                          @ApiParam("是否删除") Integer isDelete,
                                                          @ApiParam("创建人") String creator,
                                                          @ApiParam("更新人") String lastUpdater,
                                                          BigDecimal pageWidth,
                                                          BigDecimal pageHeight,
                                                          Long configDpi,
                                                          Long configUnitID,
                                                          @ApiParam("是否过滤content") @RequestParam(value = "filter", defaultValue = "true", required = false) Boolean filter,
                                                          @ApiParam("是否发布版本") @RequestParam(value = "release", required = false) Boolean release,
                                                          @ApiParam("pageOption过滤项目") @RequestParam(value = "pageOption", required = false) String pageOption) {
        return BaseResponse.success(mmTemplateService.getPage(page, limit, begin, end, status, name, mmCode, isDelete, creator, lastUpdater, pageWidth, pageHeight, configDpi, configUnitID, filter, release, pageOption));
    }

    @ApiOperation(value = "模板详情")
    @GetMapping("/{code}")
    public BaseResponse<MmTemplateVO> detail(@ApiParam("模板code") @PathVariable String code,
                                             @ApiParam("是否过滤删除page,1-是,0/null-否") Integer filter,
                                             @ApiParam("content=0时，templatePageList下的content不返回") @RequestParam(required = false) Integer content) {
        MmTemplateVO vo = mmTemplateService.getByCode(code);
        if (SHOULD_FILTER.equals(filter)) {
            vo = (MmTemplateVO) vo.filterInvalidPageAndSort();
        }
        if (ObjectUtil.equal(content, 0)) {
            vo.getTemplatePageList().forEach(x -> x.setContent(null));
        }
        return BaseResponse.success(vo);
    }

    @ApiOperation(value = "根据mmCode获取模板详情")
    @GetMapping("/mm/{mmCode}")
    public BaseResponse<MmTemplateVO> getByMmCode(@ApiParam("模板code") @PathVariable String mmCode,
                                                  @ApiParam("是否过滤删除page,1-是,0/null-否") Integer filter,
                                                  @ApiParam("content=0时，templatePageList下的content不返回") @RequestParam(required = false) Integer content) {
        MmTemplateVO vo = mmTemplateService.getByMmCode(mmCode);
        if (SHOULD_FILTER.equals(filter)) {
            vo = (MmTemplateVO) vo.filterInvalidPageAndSort();
        }
        if (ObjectUtil.equal(content, 0)) {
            vo.getTemplatePageList().forEach(x -> x.setContent(null));
        }
        return BaseResponse.success(vo);
    }

    @ApiOperation(value = "新增模板")
    @PostMapping
    public BaseResponse add(@Validated @RequestBody MmTemplate template) {
        return BaseResponse.success(mmTemplateService.add(template));
    }


    @ApiOperation(value = "修改模板")
    @PutMapping(value = "/{code}")
    public BaseResponse update(@ApiParam("模板code") @PathVariable String code, @RequestBody MmTemplate template) {
        template.setCode(code);
        return BaseResponse.success(mmTemplateService.updateByCode(code, template));
    }

    @ApiOperation(value = "修改模板")
    @PutMapping(value = "/{code}/inner")
    public BaseResponse updateUnlock(@ApiParam("模板code") @PathVariable String code, @RequestBody MmTemplate template) {
        template.setCode(code);
        return BaseResponse.success(mmTemplateService.updateUnlockByCode(template));
    }

    @ApiOperation(value = "新增/修改页面，修改时page内容带上code")
    @PutMapping(value = "/{code}/page")
    public BaseResponse patchUpdatePage(@ApiParam("模板code") @PathVariable(value = "code") String code,
                                        @RequestBody MmTemplate.MMTemplatePage page) {
        return BaseResponse.success(mmTemplateService.updatePage(code, page));
    }

    @ApiOperation(value = "删除页面")
    @DeleteMapping("/{code}/page/{pageCode}")
    public BaseResponse deletePage(@ApiParam("模板code") @PathVariable(value = "code") String code,
                                   @ApiParam("页面code") @PathVariable(value = "pageCode") String pageCode) {
        return BaseResponse.success(mmTemplateService.removePage(code, pageCode));
    }

    @ApiOperation(value = "根据code和版本号获取对应历史内容")
    @GetMapping("/{code}/draft/{version}")
    public BaseResponse<MmTemplateDraft> draft(@ApiParam("模板code") @PathVariable(value = "code") String code,
                                               @ApiParam("版本号") @PathVariable(value = "version") Integer version) {
        return BaseResponse.success(mmTemplateService.getDraft(code, version));
    }

    @ApiOperation(value = "获取所有历史版本内容")
    @GetMapping("/{code}/draft")
    public BaseResponse<List<MmTemplateDraft>> getDraftInfos(@ApiParam("模板code") @PathVariable(value = "code") String code) {
        return BaseResponse.success(mmTemplateService.getDraftInfos(code));
    }

    @ApiOperation(value = "锁定模板")
    @PutMapping(value = "/lock/{code}")
    public BaseResponse lock(@ApiParam("模板code") @PathVariable(value = "code") String code) {
        return BaseResponse.judge(mmTemplateService.lock(code), "当前模板加锁失败");
    }

    @ApiOperation(value = "复制模板")
    @PostMapping("/copy/{code}")
    public BaseResponse<MmTemplate> copy(@ApiParam("模板code") @PathVariable String code, @RequestParam(required = false) String name) {
        return BaseResponse.success(mmTemplateService.copyWithName(code, name));
    }

    @ApiOperation(value = "解锁模板")
    @PutMapping(value = "/unlock/{code}")
    public BaseResponse unlock(@ApiParam("模板code") @PathVariable(value = "code") String code) {
        return BaseResponse.judge(mmTemplateService.unlock(code), "当前模板解锁失败");
    }

    @ApiOperation(value = "解锁模板-强制")
    @PutMapping(value = "/unlock-forced/{code}")
    public BaseResponse unlockForced(@ApiParam("模板code") @PathVariable(value = "code") String code) {
        return BaseResponse.judge(mmTemplateService.unlockForced(code), "当前模板解锁失败");
    }

    @ApiOperation(value = "删除模板,状态由1改为0")
    @DeleteMapping("/{codes}")
    public BaseResponse delete(@ApiParam("code集合") @PathVariable String codes) {
        return BaseResponse.success(mmTemplateService.removeByCodes(Arrays.stream(codes.split(","))
                .collect(Collectors.toList())));
    }

    @ApiOperation(value = "mm绑定模板")
    @PostMapping("/{code}/mm/{mmCode}")
    public BaseResponse<MmTemplate> bind(@PathVariable(value = "code") String code,
                                         @PathVariable(value = "mmCode") String mmCode) {
        return BaseResponse.success(mmTemplateService.bindTemplate(code, mmCode));
    }

    @ApiOperation(value = "mm绑定模板(JSON版)")
    @PostMapping("/{code}/bindTemplateJson")
    public BaseResponse<MmTemplate> bindTemplateJson(@PathVariable(value = "code") String code,
                                                     @RequestBody MmTemplate template) {
        return BaseResponse.success(mmTemplateService.bindingTemplate(code, template));
    }

    @ApiOperation(value = "mm调整页面")
    @PutMapping("/{code}/page/{pageCode}/{sort}")
    public BaseResponse resizePage(@PathVariable(value = "code") String code,
                                   @PathVariable(value = "pageCode") String pageCode,
                                   @PathVariable(value = "sort") Integer sort) {
        return BaseResponse.success(mmTemplateService.resizePage(code, pageCode, sort));
    }

    @ApiOperation(value = "发布模板-生成新模板")
    @PostMapping("/publish/{code}")
    public BaseResponse<MmTemplate> publish(@PathVariable String code) {
        return BaseResponse.success(mmTemplateService.publish(code));
    }

    @ApiOperation(value = "发布MM")
    @PostMapping("/publish/mm")
    public BaseResponse<MmPublishRecord> publishMM(@RequestBody MmPublishReq req) {
        return BaseResponse.success(mmTemplateService.publishMM(req.getCode(), req.getPreviewUrl(), req.getPublishUrl()));
    }

    @ApiOperation(value = "获取MM的发布详情")
    @GetMapping("/publish/mm/{code}")
    public BaseResponse<MmPublishRecord> getPublishRecord(@PathVariable String code) {
        return BaseResponse.success(mmTemplateService.getPublishRecord(code));
    }

    @ApiOperation(value = "获取前缀")
    @GetMapping("/publish/mm/prefix")
    public BaseResponse<String> getPrefix() {
        return BaseResponse.success(mmTemplateService.getPrefix());
    }

    @ApiOperation(value = "获取用户设计器缓存")
    @GetMapping("/userCache/{id}")
    public BaseResponse<MmUserCache> getUserCache(@PathVariable String id) {
        return BaseResponse.success(mmTemplateService.getUserCache());
    }

    @ApiOperation(value = "保存或修改用户设计器缓存")
    @PostMapping("/userCache")
    public BaseResponse saveOrUpdateUserCache(@RequestBody MmUserCache mmUserCache) {
        mmTemplateService.saveOrUpdateUserCache(mmUserCache);
        return BaseResponse.success();
    }

    @ApiOperation(value = "脚本-初始化templatePageTotal字段")
    @GetMapping("/initTemplatePageTotal")
    public BaseResponse initTemplatePageTotal() {
        return BaseResponse.success(mmTemplateService.initTemplatePageTotal());
    }


}
