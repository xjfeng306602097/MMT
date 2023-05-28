package com.makro.mall.stat.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.stat.pojo.dto.SystemUserLogDTO;
import com.makro.mall.stat.pojo.entity.SystemUserLog;
import com.makro.mall.stat.service.SystemUserLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author jincheng
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/sys/log/user")
@Api(tags = "系统用户数据采集")
public class SysUserLogController {

    private final SystemUserLogService systemUserLogService;


    @PostMapping("page")
    @ApiOperation(value = "系统用户日志分页")
    public BaseResponse page(@RequestBody SortPageRequest<SystemUserLogDTO> req) {
        SystemUserLogDTO systemUserLog = req.getReq();
        String sortSql = req.getSortSql();
        LambdaQueryWrapper<SystemUserLog> wrapper = new LambdaQueryWrapper<SystemUserLog>()
                .likeRight(StrUtil.isNotBlank(systemUserLog.getUserIp()), SystemUserLog::getUserIp, systemUserLog.getUserIp())
                .like(StrUtil.isNotBlank(systemUserLog.getUserName()), SystemUserLog::getUserName, systemUserLog.getUserName())
                .like(StrUtil.isNotBlank(systemUserLog.getContent()), SystemUserLog::getContent, systemUserLog.getContent())
                .eq(StrUtil.isNotBlank(systemUserLog.getType()), SystemUserLog::getType, systemUserLog.getType())
                .eq(StrUtil.isNotBlank(systemUserLog.getUserId()), SystemUserLog::getUserId, systemUserLog.getUserId())
                .le(ObjectUtil.isNotNull(systemUserLog.getCreateTimeEnd()), SystemUserLog::getCreateTime, systemUserLog.getCreateTimeEnd())
                .ge(ObjectUtil.isNotNull(systemUserLog.getCreateTimeStart()), SystemUserLog::getCreateTime, systemUserLog.getCreateTimeStart())
                .last(StrUtil.isNotBlank(sortSql), sortSql);
        return BaseResponse.success(systemUserLogService.page(new MakroPage<>(req.getPage(), req.getLimit()), wrapper));
    }

}


