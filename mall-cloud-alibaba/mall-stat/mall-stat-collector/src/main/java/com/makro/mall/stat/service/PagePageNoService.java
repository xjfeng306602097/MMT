package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.pojo.snapshot.PagePageNo;

import java.util.Date;

/**
 * @author jincheng
 * @description 针对表【page_page_no】的数据库操作Service
 * @createDate 2022-08-19 15:53:15
 */
public interface PagePageNoService extends IService<PagePageNo> {

    boolean hasTimeData(Date time);
}
