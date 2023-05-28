package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.pojo.entity.MmPublishJob;

import java.util.List;
import java.util.Set;

/**
 *
 */
public interface MmPublishJobService extends IService<MmPublishJob> {

    boolean updateJobStatus(Long relatedFlow, Long status);

    Integer getMmPublishTotal(String mmCode, String channel);

    List<AssemblyDataByMemberTypeDTO> getMmPublishTotalGroupByMemberType(Set<String> mmCode);

    List<MmPublishJob> listRelated(String mmCode);

    boolean updatePublishJob(Long id, MmPublishJob job);

    List<MmPublishJob> addBatch(Long relatedFlow, String mmCode, List<MmPublishJob> jobs);
}
