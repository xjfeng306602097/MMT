package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmCustomerMembertypeMapper;
import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.pojo.entity.MmCustomerMembertype;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.admin.service.MmCustomerMembertypeService;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jincheng
 * @description 针对表【MM_CUSTOMER_MEMBERTYPE(客户关联MemberType)】的数据库操作Service实现
 * @createDate 2022-07-11 11:53:50
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MmCustomerMembertypeServiceImpl extends ServiceImpl<MmCustomerMembertypeMapper, MmCustomerMembertype>
        implements MmCustomerMembertypeService {

    @Override
    public List<AssemblyDataByMemberTypeDTO> countMemberByMemberType(Set<Long> customerIds, List<MmMemberType> memberTypes, String mmcode) {
        log.info("countMemberByMemberType 客户数组大小:{} mmcode:{}", customerIds.size(), mmcode);
        ////转换为membertypeId
        List<String> membertypes = getMembertypes(customerIds, mmcode);
        log.info("countMemberByMemberType 客户类型数组大小:{} mmcode:{}", membertypes.size(), mmcode);
        //统计
        return memberTypes.stream().map(x -> {
            AssemblyDataByMemberTypeDTO data = new AssemblyDataByMemberTypeDTO();
            data.setMmCode(mmcode);
            data.setMemberType(x.getId());
            data.setTotal(membertypes.stream().filter(y -> Objects.equals(y, x.getId())).count());
            return data;
        }).collect(Collectors.toList());
    }

    @Override
    public Set<Long> getCustomerIdsByMemberTypeIds(Set<String> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new HashSet<>();
        }
        return getBaseMapper().getSendCustomerIdsByMemberTypeIds(ids);

    }

    @NotNull
    private List<String> getMembertypes(Set<Long> customerIds, String mmcode) {
        int splitNum = 500;
        List<List<Long>> splitCustomer = Stream.iterate(0, n -> n + 1)
                .limit((customerIds.size() + splitNum - 1) / splitNum)
                .parallel()
                .map(a -> customerIds.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());

        List<String> membertypes = new ArrayList<>();
        CompletableFuture<List<String>>[] futures = new CompletableFuture[splitCustomer.size()];
        for (int i = 0; i < splitCustomer.size(); i++) {
            List<Long> x = splitCustomer.get(i);
            futures[i] = CompletableFuture.supplyAsync(() -> list(new LambdaQueryWrapper<MmCustomerMembertype>()
                            .select(MmCustomerMembertype::getMembertypeId)
                            .in(MmCustomerMembertype::getCustomerId, x))
                            .stream()
                            .map(MmCustomerMembertype::getMembertypeId).collect(Collectors.toList()))
                    .exceptionally(e -> {
                        log.error("countMemberByMemberType,customerIds:{} mmcode:{} e:{}", customerIds, mmcode, ExceptionUtil.stacktraceToString(e));
                        return new ArrayList<>();
                    });
        }
        CompletableFuture.allOf(futures).whenComplete((r, e) -> {
            for (CompletableFuture<List<String>> future : futures) {
                try {
                    membertypes.addAll(future.get());
                } catch (InterruptedException | ExecutionException ex) {
                    log.error("countMemberByMemberType2,customerIds:{} mmcode:{} e:{}", customerIds, mmcode, ExceptionUtil.stacktraceToString(e));
                    throw new BusinessException(StatusCode.UNKNOWN_EXCEPTION);
                }
            }
        }).join();
        return membertypes;
    }


}




