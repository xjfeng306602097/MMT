package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.pojo.snapshot.PagePageNo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author jincheng
 * @description 针对表【page_page_no】的数据库操作Mapper
 * @createDate 2022-08-19 15:53:15
 * @Entity .domain.PagePageNo
 */
@Mapper
public interface PagePageNoMapper extends BaseMapper<PagePageNo> {

    Boolean hasTimeData(String sql);
}




