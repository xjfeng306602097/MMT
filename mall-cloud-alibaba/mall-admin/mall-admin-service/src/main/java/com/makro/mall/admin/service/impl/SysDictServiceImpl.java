package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.makro.mall.admin.mapper.SysDictItemMapper;
import com.makro.mall.admin.mapper.SysDictMapper;
import com.makro.mall.admin.pojo.entity.SysDict;
import com.makro.mall.admin.pojo.entity.SysDictItem;
import com.makro.mall.admin.service.SysDictService;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author xiaojunfeng
 * @description 字典服务
 * @date 2021/10/13
 */
@Service
@RequiredArgsConstructor
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict>
        implements SysDictService {

    private final SysDictItemMapper sysDictItemMapper;

    @Override
    @Transactional
    public Boolean batchDelete(List<Integer> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            List<SysDictItem> sysDictItems = sysDictItemMapper.selectList(new LambdaQueryWrapper<SysDictItem>()
                    .in(SysDictItem::getParentId, idList).eq(SysDictItem::getDeleted, 0));
            if (CollectionUtil.isNotEmpty(sysDictItems)) {
                Set<String> codes = new HashSet<>();
                sysDictItems.forEach(item -> {
                    codes.add(item.getDictCode());
                });
                Assert.isTrue(CollectionUtil.isEmpty(codes), StatusCode.DICT_ITEMS_EXISTS
                        .args(Joiner.on(",").join(codes)));
            }
        }
        return removeByIds(idList);
    }
}




