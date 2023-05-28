package com.makro.mall.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.file.mapper.MmFontMapper;
import com.makro.mall.file.pojo.entity.MmFont;
import com.makro.mall.file.service.MmFontService;
import org.springframework.stereotype.Service;

/**
 * @author xiaojunfeng
 */
@Service
public class MmFontServiceImpl extends ServiceImpl<MmFontMapper, MmFont>
        implements MmFontService {

    @Override
    public boolean containsSameNameFont(String name, Long id) {
        MmFont dbFont = getOne(new LambdaQueryWrapper<MmFont>().eq(MmFont::getName, name).eq(MmFont::getStatus, 1)
                .eq(MmFont::getDeleted, 0));
        if (dbFont == null) {
            return false;
        }
        if (id != null) {
            return !dbFont.getId().equals(id);
        }
        return true;
    }

    @Override
    public boolean containsSameNameFont(String name) {
        return containsSameNameFont(name, null);
    }

}




