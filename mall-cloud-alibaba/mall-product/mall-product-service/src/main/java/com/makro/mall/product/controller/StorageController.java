package com.makro.mall.product.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.ProductStatusCode;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.vo.ProdStoragePageVO;
import com.makro.mall.product.pojo.vo.ProdStorageVO;
import com.makro.mall.product.service.ProdStorageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description: 商品主表
 * @Author: zhuangzikai
 * @Date: 2021/11/9
 **/
@Api(tags = "商品库查询接口")
@RestController
@RequestMapping("/api/v1/product")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class StorageController {
    private final ProdStorageService storageService;

    @GetMapping
    @ApiOperation(value = "商品列表分页")
    public BaseResponse<IPage<ProdStoragePageVO>> list(@ApiParam("页码") Integer page,
                                                       @ApiParam("每页数量") Integer limit,
                                                       @ApiParam("商品编码") String itemcode,
                                                       @ApiParam("英文名称") String nameen,
                                                       @ApiParam("本地语言名称") String namethai,
                                                       @ApiParam("品类ID") Integer categoryid,
                                                       @ApiParam("1：有效；0：作废删除") Integer isvalid,
                                                       @RequestParam(required = false) String segmentId) {
        ProdStorage storage = new ProdStorage();
        storage.setItemcode(itemcode);
        storage.setNameen(nameen);
        storage.setNamethai(namethai);
        storage.setCategoryid(categoryid);
        if (isvalid != null) {
            storage.setIsvalid(isvalid);
        }
        IPage<ProdStoragePageVO> result = storageService.list(new MakroPage<>(page, limit), storage, segmentId);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "新增商品库商品")
    @PostMapping(value = "/add")
    public BaseResponse<Boolean> add(@RequestBody ProdStorage storage) {
        String itemCode = storage.getItemcode();
        Assert.isTrue(StrUtil.isNotBlank(itemCode), "No Item Code.");
        ProdStorage oldStorage = storageService.getById(itemCode.trim());
        Assert.isTrue(ObjectUtil.isNull(oldStorage), "Duplicate Or Deleted Before. Please get it from the Invalid Storage.");

        storage.setId(storage.getItemcode().trim());
        storage.setIsvalid(1);
        return BaseResponse.judge(storageService.save(storage));
    }

    @ApiOperation(value = "商品库商品详情")
    @GetMapping("/{id}")
    public BaseResponse<ProdStorage> get(@PathVariable String id) {
        ProdStorage storage = storageService.getById(id);
        return BaseResponse.success(storage);
    }

    @ApiOperation(value = "更新商品库商品")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public BaseResponse<Boolean> update(@PathVariable String id, @RequestBody ProdStorage storage) {
        storage.setId(id);
        Boolean result = storageService.updateById(storage);
        return BaseResponse.judge(result);
    }

    @ApiOperation(value = "删除商品库商品")
    @DeleteMapping(value = "/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.judge(storageService.delete(id));
    }

    @ApiOperation(value = "解析excel")
    @PostMapping("/parseExcel")
    public BaseResponse<List<ProdStorageVO>> parseExcel(@RequestParam(value = "file") MultipartFile file,
                                                        @ApiParam("segment名称") @RequestParam(value = "segmentName") String segmentName) {
        return BaseResponse.success(storageService.parseExcel(file, segmentName));
    }

    @ApiOperation(value = "批量新增商品或按itemcode修改商品(Excel用)")
    @PostMapping("/createOrUpdateBatch")
    public BaseResponse<List<ProdStorageVO>> createOrUpdateBatch(@RequestBody List<ProdStorageVO> prodStorageVO) {
        Assert.isTrue(prodStorageVO.size() <= 20000, ProductStatusCode.THE_NUMBER_OF_PROD_STORAGE_IS_MORE_THAN_20000);
        //校验参数合法性
        storageService.storageValidated(prodStorageVO);
        //将传入数组拆分
        int splitNum = 500;
        List<List<ProdStorageVO>> listList = Stream.iterate(0, n -> n + 1)
                .limit((prodStorageVO.size() + splitNum - 1) / splitNum)
                .parallel()
                .map(a -> prodStorageVO.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());

        listList.forEach(storageService::createOrUpdateBatch);

        return BaseResponse.success();
    }

}
