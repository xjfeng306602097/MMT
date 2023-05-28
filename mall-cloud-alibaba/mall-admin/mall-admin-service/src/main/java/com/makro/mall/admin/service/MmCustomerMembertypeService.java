package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.pojo.entity.MmCustomerMembertype;
import com.makro.mall.admin.pojo.entity.MmMemberType;

import java.util.List;
import java.util.Set;

/**
 * @author jincheng
 * @description 针对表【MM_CUSTOMER_MEMBERTYPE(客户关联MemberType)】的数据库操作Service
 * @createDate 2022-07-11 11:53:50
 */
public interface MmCustomerMembertypeService extends IService<MmCustomerMembertype> {

    /**
     * 功能描述:
     *
     * @Param: emailCustomerIds email渠道的用户id
     * @Param: memberTypes 会员类型列表
     * @Return:
     * @Author: 卢嘉俊
     * @Date: 2022/7/11 用户行为分析
     */
    List<AssemblyDataByMemberTypeDTO> countMemberByMemberType(Set<Long> emailCustomerIds, List<MmMemberType> memberTypes, String mmcode);

    Set<Long> getCustomerIdsByMemberTypeIds(Set<String> ids);

}
