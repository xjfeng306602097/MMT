package com.makro.mall.template.controller.client;

import cn.hutool.core.util.ObjectUtil;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.template.pojo.entity.MmTemplate;
import com.makro.mall.template.service.MMTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "client 模板相关接口")
@RestController
@RequestMapping("/api/v1/template/client")
@RequiredArgsConstructor
@Slf4j
public class MmTemplateClient {


    private final MMTemplateService mmTemplateService;


    @ApiOperation(value = "根据mmCode获取模板详情", hidden = true)
    @GetMapping("/mm/{mmCode}")
    public BaseResponse<MmTemplate> getByMmCode(@PathVariable String mmCode) {
        return BaseResponse.success(mmTemplateService.findFirstByMmCodeAndIsDelete(mmCode, 0));
    }

    @PostMapping("/getByMmCodes")
    @ApiOperation(value = "根据mmCodes获取模板详情", hidden = true)
    public BaseResponse<Map<String, MmTemplate>> getByMmCodes(@RequestBody List<String> mmcodes) {
        Map<String, MmTemplate> map = new HashMap<>(16);
        List<MmTemplate> templates = mmTemplateService.findByMmCodeInAndIsDelete(mmcodes, 0);
        mmcodes.forEach(x -> {
            MmTemplate mmTemplate = templates.stream().filter(y -> ObjectUtil.equals(y.getMmCode(), x)).findFirst().orElse(null);
            map.put(x, mmTemplate);
        });
        return BaseResponse.success(map);
    }

    ;
}
