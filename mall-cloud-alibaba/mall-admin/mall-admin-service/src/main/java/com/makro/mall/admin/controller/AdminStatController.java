package com.makro.mall.admin.controller;

import cn.hutool.core.util.StrUtil;
import com.makro.mall.admin.mq.producer.StatProducer;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.web.util.IpUtil;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.message.pojo.entity.StatView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/22
 */
@RestController
@RequestMapping("/api/v1/stat")
@Api(tags = "统计视图接口")
public class AdminStatController {

    @Resource
    private StatProducer statProducer;

    @ApiOperation(value = "统计")
    @PostMapping("/view")
    public BaseResponse viewStat(@RequestBody StatView statView, HttpServletRequest request) {
        if (StrUtil.isNotEmpty(statView.getView())) {
            statView.setUserId(JwtUtils.getUserId());
            statView.setUserName(JwtUtils.getUsername());
            statView.setIp(IpUtil.getIpAddr(request));
            statProducer.sendStatMessage(statView, 10L);
            return BaseResponse.success();
        }
        return BaseResponse.error(StatusCode.ILLEGAL_STATE);
    }
}
