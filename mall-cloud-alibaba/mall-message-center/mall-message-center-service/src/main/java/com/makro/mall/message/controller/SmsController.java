package com.makro.mall.message.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.dto.SmsMessagePageFeignDTO;
import com.makro.mall.message.pojo.entity.SmsMessage;
import com.makro.mall.message.service.SmsService;
import com.makro.mall.message.vo.MessagePageReqVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 短信控制器
 * @date 2021/11/4
 */
@Api(tags = "短信相关接口")
@RestController
@RequestMapping("/api/v1/sms")
@RequiredArgsConstructor
public class SmsController {

    public static final String CONFIGURATION = "[{\"name\":\"GGG_MULTIPLE\",\"label\":\"GGG_MULTIPLE\",\"description\":\"GGG_MULTIPLE is the channel to send msg multiple\",\"options\":[{\"name\":\"smsMultipleUrl\",\"label\":\"Mutiple URL\",\"description\":\"Mutiple URL\"},{\"name\":\"smsProjectId\",\"label\":\"Project ID\",\"description\":\"Project ID\"},{\"name\":\"smsPwd\",\"label\":\"Password\",\"description\":\"Password\"},{\"name\":\"smsSender\",\"label\":\"Sender\",\"description\":\"Sender\"}]}]";

    private final SmsService smsService;

    @ApiOperation(value = "列表分页")
    @GetMapping("/page")
    public BaseResponse<Page<SmsMessage>> pageList(@ApiParam("页码") Integer page,
                                                   @ApiParam("每页数量") Integer limit,
                                                   @ApiParam("起始时间") Date begin,
                                                   @ApiParam("终止时间") Date end,
                                                   @ApiParam("状态,用,隔开") String status,
                                                   @ApiParam("名称") String name,
                                                   @ApiParam("是否删除") Integer isDelete) {
        List<Integer> statusList = new ArrayList<>();
        if (StrUtil.isNotBlank(status)) {
            int[] arrays = Arrays.stream(status.split(", ")).mapToInt(Integer::parseInt).toArray();
            statusList = Arrays.stream(arrays).boxed().collect(Collectors.toList());
        }
        return BaseResponse.success(smsService.page(statusList, name, begin, end, page, limit, isDelete));
    }

    @ApiOperation(value = "详情")
    @GetMapping("/{id}")
    public BaseResponse<SmsMessage> detail(@PathVariable String id) {
        return BaseResponse.success(smsService.findFirstById(id));
    }

    @ApiOperation(value = "新增短信")
    @PostMapping
    public BaseResponse add(@RequestBody SmsMessage smsMessage) {
        return BaseResponse.success(smsService.save(smsMessage));
    }

    @ApiOperation(value = "修改短信")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable String id,
            @RequestBody SmsMessage smsMessage) {
        smsMessage.setId(id);
        return BaseResponse.success(smsService.update(smsMessage));
    }

    @ApiOperation(value = "删除短信,状态由0改为1")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@ApiParam("id集合,用,隔开") @PathVariable String ids) {
        return BaseResponse.success(smsService.removeByIds(Arrays.stream(ids.split(","))
                .collect(Collectors.toList())));
    }

    @PostMapping("/smsUserPage")
    @ApiModelProperty(value = "smsUserPage", hidden = true)
    BaseResponse<SmsMessagePageFeignDTO> smsUserPage(@RequestBody MessagePageReqVO vo) {
        Page<SmsMessage> page = smsService.smsUserPage(vo);
        return BaseResponse.success(new SmsMessagePageFeignDTO(page.getTotalElements(), page.getContent()));
    }


    @GetMapping("/channels")
    @ApiModelProperty(value = "SmsChannelEnum", hidden = true)
    BaseResponse<JSONArray> smsChannelEnum() {
        return BaseResponse.success(JSON.parseArray(CONFIGURATION));
    }


}
