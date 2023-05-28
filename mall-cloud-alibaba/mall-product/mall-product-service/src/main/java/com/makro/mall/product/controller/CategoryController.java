package com.makro.mall.product.controller;


import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.product.pojo.entity.ProdCategory;
import com.makro.mall.product.pojo.vo.CategoryVO;
import com.makro.mall.product.pojo.vo.TreeSelectVO;
import com.makro.mall.product.service.ProdCategoryService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/21
 **/

@Api(tags = "商品类别接口")
@RestController
@RequestMapping("/api/v1/product/category")
@RequiredArgsConstructor
public class CategoryController {

    private final ProdCategoryService categoryService;

    @ApiOperation(value = "商品类别表格（Table）层级列表")
    @GetMapping("/table")
    public BaseResponse getTableList(@ApiParam("商品类别状态") Integer status,
                                     @ApiParam("商品类别名称") String name,
                                     @ApiParam("是否展开层级") boolean hasChild) {
        List<CategoryVO> categoryTableList = categoryService.listTable(status, name, hasChild);
        return BaseResponse.success(categoryTableList);
    }

    @ApiOperation(value = "商品类别下拉（TreeSelect）层级列表")
    @GetMapping("/select")
    public BaseResponse getSelectList() {
        List<TreeSelectVO> categorySelectList = categoryService.listTreeSelect();
        return BaseResponse.success(categorySelectList);
    }

    @ApiOperation(value = "商品类别列表分页")
    @GetMapping
    public BaseResponse<IPage<ProdCategory>> list(Integer page, Integer limit, String name) {
        LambdaQueryWrapper<ProdCategory> queryWrapper = new LambdaQueryWrapper<ProdCategory>()
                .like(StrUtil.isNotBlank(name), ProdCategory::getName, name)
                .orderByAsc(ProdCategory::getSort)
                .orderByDesc(ProdCategory::getGmtModified)
                .orderByDesc(ProdCategory::getGmtCreate);
        IPage<ProdCategory> result = categoryService.page(new MakroPage<>(page, limit), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "商品类别详情")
    @GetMapping("/{id}")
    public BaseResponse detail(@PathVariable String id) {
        ProdCategory ProdCategory = categoryService.getById(id);
        return BaseResponse.success(ProdCategory);
    }

    @ApiOperation(value = "新增商品类别")
    @PostMapping
    public BaseResponse add(@RequestBody ProdCategory category) {
        category.setId(IdUtil.simpleUUID());
        Boolean result = categoryService.saveCategory(category);
        return BaseResponse.judge(result);
    }

    @ApiOperation(value = "修改商品类别")
    @PutMapping(value = "/{id}")
    public BaseResponse update(@PathVariable String id, @RequestBody ProdCategory category) {
        category.setId(id);
        boolean result = categoryService.saveCategory(category);
        return BaseResponse.judge(result);
    }

    @ApiOperation(value = "删除商品类别")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable("ids") String ids) {
        return BaseResponse.judge(categoryService.deleteByIds(ids));
    }

}

