package com.makro.mall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.constant.SystemConstants;
import com.makro.mall.product.mapper.ProdCategoryMapper;
import com.makro.mall.product.pojo.entity.ProdCategory;
import com.makro.mall.product.pojo.vo.CategoryVO;
import com.makro.mall.product.pojo.vo.TreeSelectVO;
import com.makro.mall.product.service.ProdCategoryService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class ProdCategoryServiceImpl extends ServiceImpl<ProdCategoryMapper, ProdCategory>
        implements ProdCategoryService {

    @Override
    public List<CategoryVO> listTable(Integer status, String name, Boolean hasChild) {
        List<ProdCategory> categoryList = this.list(new LambdaQueryWrapper<ProdCategory>()
                .like(StrUtil.isNotBlank(name), ProdCategory::getName, name)
                .eq(status != null, ProdCategory::getStatus, status)
                .orderByAsc(ProdCategory::getSort));
        hasChild = Optional.ofNullable(hasChild).orElse(Boolean.TRUE);
        if (hasChild) {
            return recursionTableList(SystemConstants.ROOT_CATE_ID, categoryList);
        } else {
            return categoryList.stream().map(m -> BeanUtil.copyProperties(m, CategoryVO.class)).collect(Collectors.toList());
        }
    }

    @Override
    public List<TreeSelectVO> listTreeSelect() {
        List<ProdCategory> categoryList = this.list(new LambdaQueryWrapper<ProdCategory>()
                .orderByAsc(ProdCategory::getSort));
        return recursionTreeSelect(SystemConstants.ROOT_CATE_ID, categoryList);
    }

    @Override
    @Transactional
    public boolean saveCategory(ProdCategory category) {
        String treePath = getCategoryTreePath(category.getParentCode());
        category.setTreePath(treePath);
        return this.saveOrUpdate(category);
    }

    @Override
    @Transactional
    public boolean deleteByIds(String ids) {
        AtomicBoolean status = new AtomicBoolean(false);
        List<String> idList = Arrays.asList(ids.split(","));
        List<ProdCategory> categoryList = this.listByIds(idList);
        List<String> codes = categoryList.stream().map(ProdCategory::getCode).distinct().
                collect(Collectors.toList());

        Optional.ofNullable(codes).orElse(new ArrayList<>()).forEach(code -> {
            status.set(this.remove(new LambdaQueryWrapper<ProdCategory>().eq(ProdCategory::getCode, code)
                    .or()
                    .apply("CONCAT(CONCAT(',', tree_path),',') like CONCAT(CONCAT('%', {0}),'%')", code)));
        });
        return status.get();
    }

    private String getCategoryTreePath(String parentCode) {
        String treePath;
        parentCode = Optional.ofNullable(parentCode).orElse("000000");
        if (parentCode.equals(SystemConstants.ROOT_CATE_ID)) {
            treePath = String.valueOf(SystemConstants.ROOT_CATE_ID);
        } else {
            ProdCategory parentCategory = this.getOne(new LambdaQueryWrapper<ProdCategory>().eq(ProdCategory::getCode, parentCode));
            treePath = Optional.ofNullable(parentCategory).map(item -> item.getTreePath() + "," + item.getCode()).orElse(Strings.EMPTY);
        }
        return treePath;
    }

    private List<CategoryVO> recursionTableList(String parentCode, List<ProdCategory> categoryList) {
        List<CategoryVO> categoryTableList = new ArrayList<>();
        Optional.ofNullable(categoryList).orElse(new ArrayList<>())
                .stream().filter(category -> category.getParentCode().equals(parentCode))
                .forEach(category -> {
                    CategoryVO CategoryVO = new CategoryVO();
                    BeanUtil.copyProperties(category, CategoryVO);
                    List<CategoryVO> children = recursionTableList(category.getCode(), categoryList);
                    CategoryVO.setChildren(children);
                    categoryTableList.add(CategoryVO);
                });
        return categoryTableList;
    }

    private List<TreeSelectVO> recursionTreeSelect(String parentCode, List<ProdCategory> categoryList) {
        List<TreeSelectVO> treeSelectList = new ArrayList<>();
        Optional.ofNullable(categoryList).orElse(new ArrayList<>())
                .stream().filter(category -> category.getParentCode().equals(parentCode))
                .forEach(category -> {
                    TreeSelectVO CategoryVO = new TreeSelectVO();
                    CategoryVO.setCode(category.getCode());
                    CategoryVO.setName(category.getName());
                    List<TreeSelectVO> children = recursionTreeSelect(category.getCode(), categoryList);
                    CategoryVO.setChildren(children);
                    treeSelectList.add(CategoryVO);
                });
        return treeSelectList;
    }
}




