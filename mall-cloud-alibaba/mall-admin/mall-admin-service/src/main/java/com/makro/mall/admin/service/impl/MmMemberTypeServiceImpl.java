package com.makro.mall.admin.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmMemberTypeMapper;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.admin.pojo.vo.MmMemberTypeVO;
import com.makro.mall.admin.service.MmMemberTypeService;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author jincheng
* @description 针对表【MM_MEMBER_TYPE】的数据库操作Service实现
* @createDate 2023-04-18 14:39:52
*/
@Service
public class MmMemberTypeServiceImpl extends ServiceImpl<MmMemberTypeMapper, MmMemberType>
        implements MmMemberTypeService {
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public IPage<MmMemberType> page(SortPageRequest<MmMemberType> request) {
        String sortSql = request.getSortSql();
        MmMemberType req = request.getReq();
        MakroPage<MmMemberType> page = new MakroPage<>(request.getPage(), request.getLimit());
        return page(page, new LambdaQueryWrapper<MmMemberType>()
                .likeRight(StrUtil.isNotBlank(req.getNameEn()), MmMemberType::getNameEn, req.getNameEn())
                .likeRight(StrUtil.isNotBlank(req.getNameTh()), MmMemberType::getNameTh, req.getNameTh())
                .eq(ObjectUtil.isNotNull(req.getActive()), MmMemberType::getActive, req.getActive())
                .last(StrUtil.isNotEmpty(sortSql), sortSql)
        );
    }

    @Override
    public List<MmMemberType> getMembertypeByIds(Set<String> membertypeIds) {
        return listByIds(membertypeIds);
    }

    @Override
    public void script() {
        List<MmMemberTypeVO> memberTypes = redisTemplate.opsForList().range(RedisConstants.MM_MEMBER_TYPE_LIST, 0, -1);
        List<MmMemberType> mmMemberTypes = memberTypes.stream().map(x -> {
            MmMemberType mmMemberType = new MmMemberType();
            mmMemberType.setId(x.getId());
            mmMemberType.setNameTh(x.getNameTh());
            mmMemberType.setNameEn(x.getNameEn());
            mmMemberType.setActive(StrUtil.equals(x.getActive(), "true"));
            return mmMemberType;
        }).collect(Collectors.toList());

        mmMemberTypes.forEach(this::save);
    }

}




