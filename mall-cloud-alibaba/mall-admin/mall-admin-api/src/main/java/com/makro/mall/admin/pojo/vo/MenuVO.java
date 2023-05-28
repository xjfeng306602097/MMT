package com.makro.mall.admin.pojo.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.makro.mall.common.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * @author admin
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MenuVO<T> extends BaseEntity {

    private Long id;

    private Long parentId;

    private String name;

    private String icon;

    private String path;

    private String component;

    private Integer sort;

    private Integer visible;

    private String redirect;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private List<T> children;

}
