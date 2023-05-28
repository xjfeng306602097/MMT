package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.entity.SysDictItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Entity com.makro.mall.admin.pojo.entity.SysDictItem
 */
@Mapper
public interface SysDictItemMapper extends BaseMapper<SysDictItem> {

    IPage<SysDictItem> list(Page<SysDictItem> page, SysDictItem dict);

}




