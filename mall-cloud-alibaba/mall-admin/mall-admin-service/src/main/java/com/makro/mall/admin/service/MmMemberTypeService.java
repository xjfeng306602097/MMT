package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.common.model.SortPageRequest;

import java.util.List;
import java.util.Set;

/**
* @author jincheng
* @description 针对表【MM_MEMBER_TYPE】的数据库操作Service
* @createDate 2023-04-18 14:39:52
*/
public interface MmMemberTypeService extends IService<MmMemberType> {
    IPage<MmMemberType> page(SortPageRequest<MmMemberType> request);

    List<MmMemberType> getMembertypeByIds(Set<String> membertypeIds);

    void script();
}
