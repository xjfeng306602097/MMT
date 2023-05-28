package com.makro.mall.admin.controller.client;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.admin.service.MmCustomerService;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 卢嘉俊
 * @description 客户API接口
 * @date 2022/5/12
 */
@Api(tags = "客户接口")
@RestController
@RequestMapping("/api/v1/customers/client")
@Slf4j
@RequiredArgsConstructor
public class MmCustomerClient {

    private final MmCustomerService mmCustomerService;

    @PostMapping("/getVO")
    public BaseResponse<MmCustomerVO> getVO(@RequestBody MmCustomer mmCustomer) {
        MmCustomerVO vo = new MmCustomerVO();
        if (ObjectUtil.isNotNull(mmCustomer.getId())) {
            vo = mmCustomerService.getMmCustomerVOById(mmCustomer.getId());
        } else if (ObjectUtil.isNotNull(mmCustomer.getCustomerCode())) {
            vo = mmCustomerService.getMmCustomerVOByCode(mmCustomer.getCustomerCode());
        } else if (ObjectUtil.isNotNull(mmCustomer.getLineId())) {
            vo = mmCustomerService.getVOByLineId(mmCustomer.getLineId());
        }
        return BaseResponse.success(vo);
    }


    @GetMapping("/getByLineId")
    @ApiOperation(value = "")
    BaseResponse<Long> getByLineId(@RequestParam String lineUserId) {
        List<MmCustomer> customer = mmCustomerService.list(new LambdaQueryWrapper<MmCustomer>().select(MmCustomer::getId).eq(MmCustomer::getLineId, lineUserId).orderByDesc(MmCustomer::getGmtCreate));
        return BaseResponse.success(CollUtil.isEmpty(customer) ? null : customer.get(0).getId());
    }

    @PostMapping("/getByIds")
    BaseResponse<List<MmCustomer>> getByIds(@RequestBody List<String> cIds) {
        if (CollUtil.isEmpty(cIds)) {
            return BaseResponse.success(new ArrayList<>());
        }
        //将List切成500个key唯一组 上限60个
        int splitNum = 500;
        List<List<String>> splitCustomerId = Stream.iterate(0, n -> n + 1)
                .limit((cIds.size() + splitNum - 1) / splitNum)
                .parallel()
                .map(a -> cIds.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());

        List<MmCustomer> customers = new ArrayList<>();
        CompletableFuture<List<MmCustomer>>[] futures = new CompletableFuture[splitCustomerId.size()];
        for (int i = 0; i < splitCustomerId.size(); i++) {
            List<String> x = splitCustomerId.get(i);
            futures[i] = CompletableFuture.supplyAsync(() -> mmCustomerService.list(new LambdaQueryWrapper<MmCustomer>().in(MmCustomer::getId, x)))
                    .exceptionally(e -> {
                        log.error("getByIds,customerIds:{} e:{}", cIds, e);
                        return new ArrayList<>();
                    });
        }
        CompletableFuture.allOf(futures).whenComplete((r, e) -> {
            for (CompletableFuture<List<MmCustomer>> future : futures) {
                try {
                    customers.addAll(future.get());
                } catch (InterruptedException | ExecutionException ex) {
                    log.error("countMemberByMemberType2,customerIds:{} e:{}", cIds, ExceptionUtil.stacktraceToString(e));
                    throw new BusinessException(StatusCode.UNKNOWN_EXCEPTION);
                }
            }
        }).join();
        return BaseResponse.success(customers);
    }

}
