package com.makro.mall.product.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.vo.ProductVO;
import com.makro.mall.product.service.ProdListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: MM商品接口
 * @Author: zhuangzikai
 * @Date: 2021/11/10
 **/
@Api(tags = "商品接口")
@RestController
@RequestMapping("/api/v1/product/data")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class ProductController {

    private final ProdListService dataService;

    @ApiOperation(value = "MM商品列表分页")
    @GetMapping
    public BaseResponse<IPage<ProductVO>> list(@ApiParam("页码") Integer page,
                                               @ApiParam("每页数量") Integer limit,
                                               @ApiParam("商品编码") String itemcode,
                                               @ApiParam("本地语言名称") String namethai,
                                               @ApiParam("MM的Code") String mmCode,
                                               @ApiParam("MM商品导入的文件信息ID") String infoid,
                                               @ApiParam("渠道类型: classic/eco") String channelType,
                                               @ApiParam("1：有效，0：无效，被覆盖或删除") Integer isvalid,
                                               @ApiParam("海报页码") Integer mmpage,
                                               @ApiParam("海报序号") Integer mmsort,
                                               @ApiParam("英文") String nameen,
                                               @ApiParam("segmentId") Long segmentId,
                                               @ApiParam("segmentId") Long productId,
                                               @ApiParam("icons、linkItems、pic（除了info之外的其他对象）\n" +
                                                       "默认不传参查全部，为空只查info，其他的包含什么返回什么") @RequestParam(required = false) List<String> join) {
        return BaseResponse.success(dataService.selectList(page, limit, itemcode, namethai, mmCode, infoid, channelType, isvalid, mmpage, mmsort, nameen, segmentId, join, productId));
    }


    @ApiOperation(value = "新增MM商品")
    @PostMapping(value = "/add/{mmCode}")
    public BaseResponse<Boolean> add(@ApiParam("MM的Code") @PathVariable String mmCode, @RequestBody ProdList data) {
        return BaseResponse.judge(dataService.add(mmCode, data));
    }

    @ApiOperation(value = "更新MM商品")
    @PutMapping(value = "/update/{id}")
    public BaseResponse<Boolean> update(@PathVariable String id, @RequestBody ProdList data) {
        return BaseResponse.judge(dataService.updateForProd(id, data));
    }

    @ApiOperation(value = "全量更新MM商品")
    @PutMapping(value = "/updateAll/{id}")
    public BaseResponse<Boolean> updateAll(@PathVariable String id, @RequestBody ProdList data) {
        return BaseResponse.judge(dataService.updateAllForProd(id, data));
    }

    @ApiOperation(value = "更新MM商品")
    @PutMapping(value = "/updatebyItemCode")
    public BaseResponse<Boolean> update(@RequestParam String mmCode, @RequestParam String itemCode, @RequestParam String parentCode, @RequestBody ProdList data) {
        return BaseResponse.judge(dataService.updatebyItemCode(mmCode,itemCode,parentCode, data));
    }

    @ApiOperation(value = "删除MM商品")
    @DeleteMapping(value = "/{id}")
    public BaseResponse<Boolean> invalid(@PathVariable String id) {
        return BaseResponse.success(dataService.updateValid(id, 0));
    }

    @ApiOperation(value = "恢复MM商品")
    @PutMapping(value = "/restore/{id}")
    public BaseResponse<Boolean> valid(@PathVariable String id) {
        return BaseResponse.success(dataService.updateValid(id, 1));
    }

    @ApiOperation(value = "MM商品详情")
    @GetMapping("/{id}")
    public BaseResponse<ProdList> details(@PathVariable String id) {
        return BaseResponse.success(dataService.getById(id));
    }

    @ApiOperation(value = "批量获取MM商品信息")
    @GetMapping("/list/{ids}")
    public BaseResponse<List<ProductVO>> listByIds(@PathVariable String ids) {
        return BaseResponse.success(dataService.listByIdsForProd(ids));
    }

    @ApiOperation(value = "获取MM商品信息及图片")
    @GetMapping("/basicInfoAndPic/{id}")
    public BaseResponse<ProductVO> getProductWithPicture(@PathVariable String id) {
        return BaseResponse.success(dataService.getProductVO(dataService.getById(id)));
    }


    @ApiOperation(value = "根据ItemCode获取MmActivity")
    @GetMapping("/getMmActivityByItemCode/{itemCode}")
    public BaseResponse<IPage<MmActivity>> getMmActivityByItemCode(@ApiParam("itemCode") @PathVariable String itemCode,
                                                                   @ApiParam("页码") Long page,
                                                                   @ApiParam("每页数量") Long limit) {
        return BaseResponse.success(dataService.getMmActivityByItemCode(itemCode, page, limit));
    }

    @ApiOperation(value = "根据MmCode获取ProdList", hidden = true)
    @GetMapping("/getMmCodeByItemCode/{itemCode}")
    public BaseResponse<String> getMmCodeByItemCode(@PathVariable String itemCode) {
        return dataService.getMmCodeByItemCode(itemCode);
    }

    @ApiOperation(value = "填充父Id脚本")
    @GetMapping("/parentIdScript")
    public BaseResponse<Boolean> parentIdScript() {
        return BaseResponse.success(dataService.parentIdScript());
    }

}
