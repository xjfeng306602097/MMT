package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.pojo.vo.MmSegmentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Ljj
 * @description 针对表【MM_SEGMENT(SEGMENT)】的数据库操作Mapper
 * @createDate 2022-05-12 17:18:38
 * @Entity com.makro.mall.admin.pojo.entity.MmSegment
 */
@Mapper
public interface MmSegmentMapper extends BaseMapper<MmSegment> {

    List<MmSegment> list(Page<MmSegmentVO> page, MmSegmentVO mmSegmentVO, String sortSql);

    Long maxId();

}




