package com.makro.mall.product.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/21
 **/
@Data
public class CategoryVO {
    /**
     * ID
     */
    private String id;

    /**
     * 类别名称
     */
    private String name;

    /**
     * 类别编码
     */
    private String code;

    /**
     * 层级
     */
    private String catlevel;

    /**
     * 父类编码
     */
    private String parentCode;

    /**
     * 父类层级
     */
    private String parentLevel;

    /**
     * 排序
     */
    private String sort;

    /**
     * 状态
     */
    private Long status;

    /**
     * 编码树形路径
     */
    private String treePath;

    /**
     * 是否有效
     */
    private Long isvalid;

    /**
     * 子类
     */
    private List<CategoryVO> children;
}
