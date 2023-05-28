package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.entity.MmCustomerSegment;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.pojo.vo.MmSegmentVO;

import java.util.List;
import java.util.Set;

/**
 * @author Ljj
 * @description 针对表【MM_CUSTOMER_SEGMENT(客户关联SEGMENT表)】的数据库操作Service
 * @createDate 2022-05-12 17:18:19
 */
public interface MmCustomerSegmentService extends IService<MmCustomerSegment> {

    Set<MmSegment> getSegmentsByCustomerId(Long customerId);

    Set<MmSegmentVO> getMmSegmentVOSByCustomerId(Long id);

    List<MmCustomer> getCustomersBySegmentIds(Set<Long> segmentIds, String filter);

    Long getPrimaryKey(String key);

    List<MmCustomer> getMmCustomerBySegmentId(Set<Long> segmentId);

    Set<Long> getCustomerIdsBySegmentIds(Set<Long> ids);

}
