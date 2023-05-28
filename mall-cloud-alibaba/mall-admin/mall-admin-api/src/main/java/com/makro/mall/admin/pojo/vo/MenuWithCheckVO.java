package com.makro.mall.admin.pojo.vo;

import lombok.Data;

/**
 * @author xiaojunfeng
 * @description 带判断当前角色是否有关联菜单
 * @date 2021/10/21
 */
@Data
public class MenuWithCheckVO extends MenuVO<MenuWithCheckVO> {

    private Boolean checked = false;

}
