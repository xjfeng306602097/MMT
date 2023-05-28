package com.makro.mall.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmCustomerPageReqVO;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.admin.pojo.vo.VerifyCustomerRepVO;
import com.makro.mall.admin.service.MmCustomerService;
import com.makro.mall.common.model.*;
import com.makro.mall.common.util.AesBase62Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 卢嘉俊
 * @description 客户API接口
 * @date 2022/5/12
 */
@Api(tags = "客户接口")
@RestController
@RequestMapping("/api/v1/customers")
@Slf4j
@RequiredArgsConstructor
public class MmCustomerController {

    private final MmCustomerService mmCustomerService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmCustomerVO>> page(@RequestBody SortPageRequest<MmCustomerPageReqVO> request) {
        return BaseResponse.success(mmCustomerService.page(new MakroPage<>(request.getPage(), request.getLimit()), request.getReq(), request.getSortSql()));
    }

    @ApiOperation(value = "客户详情")
    @GetMapping("/{id}")
    public BaseResponse<MmCustomerVO> detail(@PathVariable Long id) {
        MmCustomerVO customer = mmCustomerService.getMmCustomerVOById(id);
        if (customer == null) {
            return BaseResponse.error(AdminStatusCode.CUSTOMER_IS_EMPTY);
        }
        return BaseResponse.success(customer);
    }

    @ApiOperation(value = "批量新增客户或按手机号码更新用户")
    @PostMapping
    public BaseResponse<List<MmCustomerVO>> createOrUpdateBatch(@RequestBody List<MmCustomerVO> customers) {
        Assert.isTrue(customers.size() <= 20000, AdminStatusCode.THE_NUMBER_OF_CUSTOMERS_IS_MORE_THAN_20000);
        //校验参数合法性
        mmCustomerService.customerValidated(customers);
        //将传入数组拆分
        int splitNum = 500;
        List<List<MmCustomerVO>> listList = Stream.iterate(0, n -> n + 1)
                .limit((customers.size() + splitNum - 1) / splitNum)
                .parallel()
                .map(a -> customers.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());

        listList.forEach(mmCustomerService::createOrUpdateBatch);

        return BaseResponse.success();
    }

    @ApiOperation(value = "新增客户")
    @PostMapping("/add")
    public BaseResponse<List<MmCustomerVO>> create(@RequestBody MmCustomerVO customers) {
        List<MmCustomerVO> customers1 = List.of(customers);
        //校验参数合法性
        mmCustomerService.customerValidated(customers1);
        Assert.notTrue(mmCustomerService.getBaseMapper().exists(new LambdaQueryWrapper<MmCustomer>().eq(MmCustomer::getPhone, customers.getPhone())), AdminStatusCode.CUSTOMER_IS_EXISTS);

        mmCustomerService.createOrUpdateBatch(customers1);

        return BaseResponse.success();
    }

    @ApiOperation(value = "解析excel" +
            "导入时如果没有name则将会员号转为name")
    @PostMapping("/parseExcel")
    public BaseResponse<List<MmCustomerVO>> parseExcel(@ApiParam("excel文件") MultipartFile file,
                                                       @RequestParam(required = false) Long segmentId,
                                                       @ApiParam("line配置类的ChannelToken") @RequestParam(required = false) String lineBotChannelToken) {
        return BaseResponse.success(mmCustomerService.parseExcel(file, segmentId, lineBotChannelToken));
    }

    @ApiOperation(value = "删除客户")
    @DeleteMapping("/{ids}")
    public BaseResponse<Boolean> delete(@PathVariable String ids) {
        return BaseResponse.judge(mmCustomerService.delete(ids));
    }

    @ApiOperation(value = "删除客户与segment关系")
    @DeleteMapping("/{ids}/segment/{segmentId}")
    public BaseResponse<Boolean> deleteSegment(@PathVariable String ids,
                                               @PathVariable String segmentId) {
        return BaseResponse.judge(mmCustomerService.deleteSegment(ids, segmentId));
    }

    @ApiOperation(value = "选择性更新客户")
    @PatchMapping(value = "/{id}")
    public BaseResponse<Boolean> patch(@PathVariable Long id, @RequestBody MmCustomerVO customerVO) {
        customerVO.setId(id);
        mmCustomerService.updateCustomer(customerVO);
        return BaseResponse.success();
    }

    @ApiOperation(value = "检验用户接口")
    @GetMapping(value = "/verifyCustomer")
    public BaseResponse<VerifyCustomerRepVO> verifyCustomer(@ApiParam("customerCode/phone 用逗号隔开") String data,
                                                            @ApiParam("传入 customerCode/phone") String field) {
        return BaseResponse.success(mmCustomerService.verifyCustomer(data, field));
    }

    @ApiOperation(value = "会员编码解密")
    @GetMapping("/encrypt")
    public BaseResponse<String> encrypt(String code) {
        return BaseResponse.success(AesBase62Util.decode(code));
    }

}
