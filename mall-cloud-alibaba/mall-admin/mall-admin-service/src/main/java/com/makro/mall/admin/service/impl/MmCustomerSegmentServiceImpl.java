package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmCustomerMapper;
import com.makro.mall.admin.mapper.MmCustomerSegmentMapper;
import com.makro.mall.admin.mapper.MmSegmentMapper;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.entity.MmCustomerSegment;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.pojo.vo.MmSegmentVO;
import com.makro.mall.admin.service.MmCustomerSegmentService;
import com.makro.mall.message.api.MessagePropertiesFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Ljj
 * @description 针对表【MM_CUSTOMER_SEGMENT(客户关联SEGMENT表)】的数据库操作Service实现
 * @createDate 2022-05-12 17:18:19
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MmCustomerSegmentServiceImpl extends ServiceImpl<MmCustomerSegmentMapper, MmCustomerSegment> implements MmCustomerSegmentService {

    public final static String CUSTOMER_PRIMARY_KEY = "primaryKey:customer";
    public final static String SEGMENT_PRIMARY_KEY = "primaryKey:segment";
    private final MessagePropertiesFeignClient messagePropertiesFeignClient;
    private final RedisTemplate redisTemplate;
    private final MmCustomerMapper mmCustomerMapper;
    private final MmSegmentMapper mmSegmentMapper;

    @Override
    public Set<MmSegment> getSegmentsByCustomerId(Long customerId) {
        return baseMapper.getSegmentsByCustomerId(customerId);
    }

    @Override
    public Set<MmSegmentVO> getMmSegmentVOSByCustomerId(Long customerId) {
        Set<MmSegment> segments = getSegmentsByCustomerId(customerId);
        return segments.stream().map(x -> {
            MmSegmentVO mmSegmentVO = new MmSegmentVO();
            BeanUtil.copyProperties(x, mmSegmentVO);
            return mmSegmentVO;
        }).collect(Collectors.toSet());
    }

    @Override
    public List<MmCustomer> getCustomersBySegmentIds(Set<Long> segmentIds, String filter) {
        if (CollUtil.isEmpty(segmentIds)) {
            return ListUtil.empty();
        }
        String lineBotChannelToken = null;
        if (StrUtil.equals(filter, "lineId")) {
            lineBotChannelToken = messagePropertiesFeignClient.detail("1").getData().getLineBotChannelToken();
        }
        return baseMapper.getCustomersBySegmentIds(segmentIds, filter, lineBotChannelToken);

    }

    @Override
    public Long getPrimaryKey(String key) {
        Long primaryKey = redisTemplate.opsForValue().increment(key);
        if (primaryKey == null || primaryKey == 1) {
            //获取数据库最大id
            if (CUSTOMER_PRIMARY_KEY.equals(key)) {
                primaryKey = mmCustomerMapper.maxId();
            } else if (SEGMENT_PRIMARY_KEY.equals(key)) {
                primaryKey = mmSegmentMapper.maxId();
            }
            //设置缓存
            if (null == primaryKey) {
                log.error("查询不到MmCustomer表中数据,进行初始化");
                primaryKey = 1L;
            } else {
                primaryKey++;
            }
            redisTemplate.opsForValue().set(key, primaryKey);
        }
        log.info("Redis获取主键为key:{} value:{}", key, primaryKey);
        return primaryKey;
    }

    @Override
    public List<MmCustomer> getMmCustomerBySegmentId(Set<Long> segmentId) {
        return getBaseMapper().getCustomersBySegmentIds(segmentId, null, null);
    }

    @Override
    public Set<Long> getCustomerIdsBySegmentIds(Set<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new HashSet<>();
        }
        return getBaseMapper().getSendCustomerIdsBySegmentIds(ids);
    }


}




