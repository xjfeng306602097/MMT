package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.admin.pojo.entity.MmCustomerMembertype;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

/**
 * @author jincheng
 * @description 针对表【MM_CUSTOMER_MEMBERTYPE(客户关联MemberType)】的数据库操作Mapper
 * @createDate 2022-07-29 15:32:16
 * @Entity .domain.MmCustomerMembertype
 */
@Mapper
public interface MmCustomerMembertypeMapper extends BaseMapper<MmCustomerMembertype> {


    Set<Long> getSendCustomerIdsByMemberTypeIds(Set<String> ids);

}




