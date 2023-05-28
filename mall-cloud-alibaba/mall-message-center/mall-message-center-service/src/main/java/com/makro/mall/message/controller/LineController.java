package com.makro.mall.message.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.admin.api.CustomerFeignClient;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.message.dto.ChatQueueDTO;
import com.makro.mall.message.dto.LineMessagePageFeignDTO;
import com.makro.mall.message.pojo.entity.LineSendMessage;
import com.makro.mall.message.sdk.line.LineSDK;
import com.makro.mall.message.service.LineService;
import com.makro.mall.message.vo.MessagePageReqVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jincheng
 */
@RestController
@RequestMapping("/api/v1/line")
@RequiredArgsConstructor
@Api(tags = "Line相关接口")
public class LineController {

    private final LineSDK lineSDK;
    private final LineService lineService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomerFeignClient customerFeignClient;


    @PostMapping("/multicastFlex")
    @ApiOperation("json推送")
    public BaseResponse<Boolean> multicastFlex(@RequestBody LineSendMessage lineSendMessage) {
        return BaseResponse.judge(lineService.multicastFlex(lineSendMessage));
    }

    @PostMapping("/broadcast")
    @ApiOperation("json群发")
    public BaseResponse<Boolean> broadcast(@RequestBody String message) {
        return BaseResponse.judge(lineSDK.broadcastFlex(message));
    }


    @PostMapping("/page")
    @ApiOperation(value = "page", hidden = true)
    public BaseResponse<LineMessagePageFeignDTO> linePage(@RequestBody MessagePageReqVO vo) {
        Page<LineSendMessage> page = lineService.page(vo);
        return BaseResponse.success(new LineMessagePageFeignDTO(page.getTotalElements(), page.getContent()));
    }

    @GetMapping("/search")
    @ApiOperation(value = "search")
    public BaseResponse<List<JSONObject>> search(String key) {
        return BaseResponse.success(Arrays.stream(key.split(",")).map(lineService::search).collect(Collectors.toList()));
    }

    @GetMapping("/chatMember")
    @ApiOperation(value = "查找客户对话列表")
    public BaseResponse<List<MmCustomerVO>> chatMember() {
        List<Object> range = redisTemplate.opsForList().range("demo:chatMember", 0, -1);
        if (CollUtil.isEmpty(range)) {
            return BaseResponse.success(ListUtil.empty());
        }

        List<MmCustomerVO> collect = range.stream().map(x -> customerFeignClient.getVO(new MmCustomer().setCustomerCode((String) x)).getData()).collect(Collectors.toList());
        return BaseResponse.success(collect);
    }

    @GetMapping("/chatQueue")
    @ApiOperation(value = "查找客户对话记录")
    public BaseResponse<List<Object>> chatQueue(String lineId, long start, long end) {
        List<Object> range = redisTemplate.opsForList().range("demo:chatQueue:" + lineId, start, end);
        if (CollUtil.isEmpty(range)) {
            return BaseResponse.success(ListUtil.empty());
        }
        for (int i = 0; i < range.size(); i++) {
            if (range.get(i) instanceof ChatQueueDTO) {

                ((ChatQueueDTO) range.get(i)).setIndex(i + start);
            }
        }

        return BaseResponse.success(range);
    }

    @GetMapping("/input")
    @ApiOperation(value = "测试放入")
    public BaseResponse input(String lineId, String message, Boolean isUser) {
        lineService.demoSave(lineId, message, isUser);
        return BaseResponse.success();
    }
}
