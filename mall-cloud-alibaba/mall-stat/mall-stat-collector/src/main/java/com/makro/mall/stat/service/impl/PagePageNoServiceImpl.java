package com.makro.mall.stat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.mapper.PagePageNoMapper;
import com.makro.mall.stat.pojo.snapshot.PagePageNo;
import com.makro.mall.stat.service.PagePageNoService;
import com.makro.mall.stat.util.ClickHouseDateUtil;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author jincheng
 * @description 针对表【page_page_no】的数据库操作Service实现
 * @createDate 2022-08-19 15:53:15
 */
@Service
public class PagePageNoServiceImpl extends ServiceImpl<PagePageNoMapper, PagePageNo>
        implements PagePageNoService {

    @Override
    public boolean hasTimeData(Date time) {
        return ObjectUtil.equals(getBaseMapper().hasTimeData(ClickHouseDateUtil.format2Str(time)), true);

    }
}




