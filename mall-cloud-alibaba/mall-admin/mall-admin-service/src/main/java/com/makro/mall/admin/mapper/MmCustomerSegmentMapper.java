package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.entity.MmCustomerSegment;
import com.makro.mall.admin.pojo.entity.MmSegment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author Ljj
 * @description 针对表【MM_CUSTOMER_SEGMENT(客户关联SEGMENT表)】的数据库操作Mapper
 * @createDate 2022-05-12 17:18:19
 * @Entity com.makro.mall.admin.pojo.entity.MmCustomerSegment
 */
@Mapper
public interface MmCustomerSegmentMapper extends BaseMapper<MmCustomerSegment> {

    Set<MmSegment> getSegmentsByCustomerId(Long customerId);

    List<MmCustomer> getCustomersBySegmentIds(Set<Long> ids, String filter, String lineBotChannelToken);

    Set<Long> getSendCustomerIdsBySegmentIds(Set<Long> ids);

}




