package com.makro.mall.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.file.pojo.entity.MmFont;

/**
 *
 */
public interface MmFontService extends IService<MmFont> {

    boolean containsSameNameFont(String name, Long id);

    boolean containsSameNameFont(String name);
}
