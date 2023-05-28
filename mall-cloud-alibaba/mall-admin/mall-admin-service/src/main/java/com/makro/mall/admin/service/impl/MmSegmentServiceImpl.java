package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmSegmentMapper;
import com.makro.mall.admin.mq.producer.InvalidSegmentProducer;
import com.makro.mall.admin.pojo.entity.MmCustomerSegment;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.pojo.vo.MmSegmentVO;
import com.makro.mall.admin.service.MmCustomerSegmentService;
import com.makro.mall.admin.service.MmSegmentService;
import com.makro.mall.common.model.AdminStatusCode;
import com.makro.mall.common.model.Assert;
import com.makro.mall.product.api.ProdStorageFeignClient;
import com.makro.mall.product.pojo.dto.ItemCodeSegmentDelDTO;
import com.makro.mall.product.pojo.entity.ItemCodeSegment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ljj
 * @description 针对表【MM_SEGMENT(SEGMENT)】的数据库操作Service实现
 * @createDate 2022-05-12 17:18:38
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MmSegmentServiceImpl extends ServiceImpl<MmSegmentMapper, MmSegment>
        implements MmSegmentService {
    private final InvalidSegmentProducer invalidSegmentProducer;
    private final MmCustomerSegmentService mmCustomerSegmentService;

    private final ProdStorageFeignClient prodStorageFeignClient;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(MmSegmentVO segmentVO) {
        MmSegment segment = getOne(new LambdaQueryWrapper<MmSegment>().eq(MmSegment::getName, segmentVO.getName()));
        if (segment == null) {
            MmSegment mmSegment = new MmSegment();
            BeanUtil.copyProperties(segmentVO, mmSegment);
            mmSegment.setInvalid(0L);
            mmSegment.setId(mmCustomerSegmentService.getPrimaryKey(MmCustomerSegmentServiceImpl.SEGMENT_PRIMARY_KEY));
            save(mmSegment);
            return mmSegment.getId();
        }
        return segment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBySegmentVO(MmSegmentVO segmentVO) {
        Assert.isTrue(ObjectUtil.isNotNull(segmentVO.getId()), AdminStatusCode.SEGMENT_ID_IS_EMPTY);
        MmSegment segment = getById(segmentVO.getId());
        if (ObjectUtil.isNotNull(segmentVO.getInvalid())) {
            Assert.isTrue(ObjectUtil.equal(segment.getInvalid(), 0L), AdminStatusCode.CANNOT_OPEN_SEGMENT);
        }
        if (StrUtil.isNotBlank(segmentVO.getName())) {
            MmSegment one = getOne(new LambdaQueryWrapper<MmSegment>().eq(MmSegment::getName, segmentVO.getName()));
            Assert.isTrue(ObjectUtil.isNull(one) || StrUtil.equals(segment.getName(), segmentVO.getName()), AdminStatusCode.NAME_ALREADY_EXISTS);
        }
        MmSegment mmSegment = new MmSegment();
        BeanUtil.copyProperties(segmentVO, mmSegment);
        baseMapper.updateById(mmSegment);
    }

    @Override
    public MmSegmentVO getMmSegmentById(Long id) {
        MmSegment segment = getById(id);
        MmSegmentVO mmSegmentVO = new MmSegmentVO();
        BeanUtil.copyProperties(segment, mmSegmentVO);
        return mmSegmentVO;
    }

    @Override
    public IPage<MmSegmentVO> page(Page<MmSegmentVO> page, MmSegmentVO mmSegmentVO, String sortSql) {
        List<MmSegment> list = this.baseMapper.list(page, mmSegmentVO, sortSql);

        List<MmSegmentVO> collect = list.stream().map(x -> {
            MmSegmentVO segmentVO = new MmSegmentVO();
            BeanUtil.copyProperties(x, segmentVO);
            return segmentVO;
        }).collect(Collectors.toList());


        page.setRecords(collect);
        return page;

    }

    @Override
    public Boolean delete(String ids) {
        List<Long> collect = Arrays.stream(ids.split(",")).map(Long::valueOf).collect(Collectors.toList());
        removeByIds(collect);
        //删除用户segment关联表
        mmCustomerSegmentService.remove(new LambdaQueryWrapper<MmCustomerSegment>().in(MmCustomerSegment::getSegmentId,collect));
        //删除商品segment关联表
        prodStorageFeignClient.itemCodeSegment(new ItemCodeSegmentDelDTO(null,collect));
        return true;
    }

    @Override
    public List<MmSegment> getMmSegmentsNotInvalidByIds(Set<Long> segmentIds) {
        return list(new LambdaQueryWrapper<MmSegment>().in(MmSegment::getId, segmentIds).eq(MmSegment::getInvalid, 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(Set<MmSegment> updateSegments) {
        updateBatchById(updateSegments);
    }


    @Override
    public Boolean invalidSegmentHandler() {
        //现在之前的没失效的马上失效
        update(new LambdaUpdateWrapper<MmSegment>()
                .eq(MmSegment::getInvalid, 0L)
                .le(MmSegment::getEndTime, LocalDateTime.now())
                .set(MmSegment::getInvalid, 1L));

        //2天之内的放入队列等待失效
        LocalDateTime tomorrow = LocalDateTimeUtil.offset(LocalDateTime.now(), 2, ChronoUnit.DAYS);
        List<MmSegment> list = list(new LambdaQueryWrapper<MmSegment>()
                .eq(MmSegment::getInvalid, 0L)
                .isNotNull(MmSegment::getEndTime)
                .le(MmSegment::getEndTime, tomorrow));

        list.forEach(x -> {
            //进入队列
            invalidSegmentProducer.sendInvalidSegmentMessage(x, x.getEndTime());
        });
        return true;
    }

    @Override
    public Long getIdIfNullCreateThat(String segmentName) {
        MmSegmentVO mmSegmentVO = new MmSegmentVO();
        mmSegmentVO.setName(segmentName);
        return create(mmSegmentVO);
    }


}




