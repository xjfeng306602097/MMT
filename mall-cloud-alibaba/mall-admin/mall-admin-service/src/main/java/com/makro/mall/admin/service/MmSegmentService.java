package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.pojo.vo.MmSegmentVO;

import java.util.List;
import java.util.Set;

/**
 * @author Ljj
 * @description 针对表【MM_SEGMENT(SEGMENT)】的数据库操作Service
 * @createDate 2022-05-12 17:18:38
 */
public interface MmSegmentService extends IService<MmSegment> {

    Long create(MmSegmentVO segmentVO);

    void updateBySegmentVO(MmSegmentVO segmentVO);

    MmSegmentVO getMmSegmentById(Long id);

    IPage<MmSegmentVO> page(Page<MmSegmentVO> page, MmSegmentVO mmSegmentVO, String sortSql);

    Boolean delete(String ids);

    List<MmSegment> getMmSegmentsNotInvalidByIds(Set<Long> segmentIds);

    void updateBatch(Set<MmSegment> updateSegments);

    Boolean invalidSegmentHandler();

    Long getIdIfNullCreateThat(String segmentName);
}
