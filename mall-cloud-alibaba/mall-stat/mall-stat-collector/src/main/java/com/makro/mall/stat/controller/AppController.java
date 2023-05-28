package com.makro.mall.stat.controller;

import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.stat.service.AppUvLogService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Set;


/**
 * @author jincheng
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1/app")
@Api(hidden = true)
public class AppController {

    private final AppUvLogService appUvLogService;
    @GetMapping("/getTotalByTime")
    public BaseResponse<Set<String>> getTotalByTime(@RequestParam("startTime") Date startTime, @RequestParam("endTime") Date endTime) {
        return BaseResponse.success(appUvLogService.getTotalByTime(startTime, endTime));
    }
}


