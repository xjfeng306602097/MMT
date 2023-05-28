package com.makro.mall.product.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.common.utils.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.*;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.file.api.PicFeignClient;
import com.makro.mall.file.pojo.dto.PicUploadDTO;
import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ProdPic;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.vo.PicNotTempReqVO;
import com.makro.mall.product.pojo.vo.ProdPicVO;
import com.makro.mall.product.service.ProdPicService;
import com.makro.mall.product.service.ProdStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 商品图片
 * @date 2021/11/24
 */
@Api(tags = "商品图片")
@RestController
@RequestMapping("/api/v1/product/pic")
@Slf4j
@RefreshScope
public class ProdPicController {

    @Resource
    private ProdPicService prodPicService;

    @Resource
    private ProdStorageService storageService;

    @Autowired
    @Qualifier(value = "productExecutor")
    private Executor productExecutor;

    @Resource
    private PicFeignClient picFeignClient;

    @Value("${max.upload.count:30}")
    private Integer maxUploadCount;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<PicProperties<ProdPicVO>>> pageList(@RequestBody SortPageRequest<ProdPic> request) {
        String sortSql = request.getSortSql();
        ProdPic req = request.getReq();
        LambdaQueryWrapper<ProdPic> queryWrapper = new LambdaQueryWrapper<ProdPic>()
                .eq(ProdPic::getDeleted, 0L)
                .eq(ObjectUtil.isNotNull(req) && ObjectUtil.isNotNull(req.getDefaulted()), ProdPic::getDefaulted, req.getDefaulted())
                .eq(ObjectUtil.isNotNull(req) && ObjectUtil.isNotNull(req.getItemCode()), ProdPic::getItemCode, req.getItemCode())
                .eq(ObjectUtil.isNotNull(req) && ObjectUtil.isNotNull(req.getTemp()), ProdPic::getTemp, req.getTemp())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<ProdPic> result = prodPicService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        IPage<PicProperties<ProdPicVO>> resultVo = result.convert(PicProperties::convert);
        return BaseResponse.success(resultVo);
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<PicProperties<ProdPicVO>> detail(@PathVariable Long id) {
        return BaseResponse.success(PicProperties.convert(prodPicService.getById(id)));
    }

    @ApiOperation(value = "新增")
    @PostMapping
    public BaseResponse add(@RequestBody ProdPic pic) {
        return BaseResponse.judge(prodPicService.add(pic));
    }

    @ApiOperation(value = "修改")
    @PutMapping(value = "/{id}")
    @Transactional
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody ProdPic pic) {
        pic.setId(id);
        if (pic.getDefaulted() != null) {
            // 查出所有itemCode相关的图片
            List<ProdPic> picList = prodPicService.listRelatedPicById(id);
            if (pic.getDefaulted() == 1L) {
                // 调为默认
                for (ProdPic prodPic : picList) {
                    if (prodPic.getDefaulted() == 1L && !prodPic.getId().equals(id)) {
                        prodPic.setDefaulted(0L);
                        // 直接跳出，只允许一条记录更新
                        prodPicService.updateById(prodPic);
                    }
                }
            } else {
                // 检查是否存在默认图片
                Assert.isTrue(picList.stream().anyMatch(p -> p.getDefaulted() == 1L && !p.getId()
                        .equals(pic.getId())), "product.picture.defaulted.required");
            }
            return BaseResponse.judge(prodPicService.updateById(pic));
        } else {
            return BaseResponse.judge(prodPicService.updateById(pic));
        }
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(prodPicService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList())));
    }

    @ApiOperation(value = "批量上传")
    @PostMapping("/batch/upload")
    public BaseResponse batchUpload(@RequestParam("files") MultipartFile[] files) throws IOException {
        Set<ProdStorage> storageSet = new HashSet<>();
        String userName = JwtUtils.getUsername();
        Assert.isTrue(files.length < maxUploadCount, StatusCode.MAX_UPLOAD_COUNT.args(maxUploadCount));
        Set<String> errorSet = new HashSet<>();
        List<CompletableFuture<BaseResponse<PicUploadDTO>>> futureList = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.getOriginalFilename() == null || !file.getOriginalFilename().contains("_")) {
                errorSet.add(file.getOriginalFilename());
                continue;
            }
            String itemCode = file.getOriginalFilename().split("_")[0];
            // 判断是否有对应的商品
            ProdStorage storage = storageService.getOne(new LambdaQueryWrapper<ProdStorage>().eq(ProdStorage::getItemcode, itemCode));
            if (storage == null) {
                storage = new ProdStorage();
                storage.setIsvalid(0);
                storage.setItemcode(itemCode);
                // 通过set避免重复
                storageSet.add(storage);
            }
            futureList.add(CompletableFuture.supplyAsync(() -> {
                // 调用file上传接口获取商品图片
                return picFeignClient.upload(file);
            }, productExecutor).thenApplyAsync(result -> {
                if (result != null && result.getData() != null) {
                    log.info("调用上传接口成功,返回体{}", result.getData());
                    // 上传成功,插入商品图片
                    processSavePic(userName, itemCode, result);
                } else {
                    log.warn("调用上传接口成功，但返回data为空, 返回体{}", JSON.toJSONString(result));
                }
                return result;
            }, productExecutor).exceptionally(e -> {
                log.error("调用上传图片接口失败,图片名称{}", file.getOriginalFilename(), e);
                return null;
            }));
        }
        // 主线程等待
        futureList.forEach(CompletableFuture::join);
        // 批量插入
        storageService.saveBatch(storageSet);
        return BaseResponse.judge(errorSet.size() == 0, StatusCode.WRONG_FILE_NAME
                .args(StringUtils.join(errorSet, ",")));
    }

    private void processSavePic(String userName, String itemCode, BaseResponse<PicUploadDTO> result) {
        ProdPic pic = prodPicService.getBaseMapper().selectOne(new LambdaQueryWrapper<ProdPic>()
                .eq(ProdPic::getItemCode, itemCode).eq(ProdPic::getDefaulted, 1));
        if (pic != null) {
            pic = new ProdPic();
            pic.setDefaulted(0L);
        } else {
            pic = new ProdPic();
            pic.setDefaulted(1L);
        }
        pic.setItemCode(itemCode);
        pic.setCreator(userName);
        pic.setLastUpdater(userName);
        pic.setFilePath(JSON.toJSONString(result.getData()));
        prodPicService.save(pic);
    }

    @ApiOperation(value = "批量将商品图片的临时标志字段，改回正常图片")
    @PostMapping("/notTemp")
    public BaseResponse notTemp(@RequestBody PicNotTempReqVO vo) {
        return BaseResponse.judge(prodPicService.update(new LambdaUpdateWrapper<ProdPic>().set(ProdPic::getTemp, 0).in(ProdPic::getId, vo.getIds())));
    }


}
