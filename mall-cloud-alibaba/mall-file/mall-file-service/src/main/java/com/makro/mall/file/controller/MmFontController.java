package com.makro.mall.file.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.*;
import com.makro.mall.common.util.ByteConvertUtil;
import com.makro.mall.file.pojo.entity.MmFont;
import com.makro.mall.file.service.MmFontService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description 字体控制器
 * @date 2021/10/26
 */
@Api(tags = "字体接口")
@RestController
@RequestMapping("/api/v1/fonts")
@RequiredArgsConstructor
@Slf4j
public class MmFontController {

    private final MmFontService mmFontService;

    @ApiOperation(value = "列表分页")
    @PostMapping("/page")
    public BaseResponse<IPage<MmFont>> pageList(@RequestBody(required = false) SortPageRequest<MmFont> request) {
        String sortSql = request.getSortSql();
        MmFont font = request.getReq();
        LambdaQueryWrapper<MmFont> queryWrapper = new LambdaQueryWrapper<MmFont>()
                .eq(MmFont::getDeleted, 0)
                .eq(font.getStatus() != null, MmFont::getStatus, font.getStatus())
                .like(StrUtil.isNotBlank(font.getName()), MmFont::getName, font.getName())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<MmFont> result = mmFontService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        return BaseResponse.success(result);
    }

    @ApiOperation(value = "字体详情")
    @GetMapping("/{id}")
    public BaseResponse<MmFont> detail(@PathVariable Long id) {
        return BaseResponse.success(mmFontService.getById(id));
    }

    @ApiOperation(value = "新增字体")
    @PostMapping
    public BaseResponse add(@RequestBody MmFont font) {
        font.setStatus(1);
        font.setDeleted(0);
        Assert.notTrue(StrUtil.isNotEmpty(font.getName()) && mmFontService.containsSameNameFont(font.getName()),
                StatusCode.FONT_NAME_DUPLICATE);
        return BaseResponse.judge(mmFontService.save(font));
    }

    @ApiOperation(value = "修改字体")
    @PutMapping(value = "/{id}")
    public BaseResponse update(
            @PathVariable Long id,
            @RequestBody MmFont font) {
        font.setId(id);
        Assert.notTrue(StrUtil.isNotEmpty(font.getName()) && mmFontService.containsSameNameFont(font.getName(), id), StatusCode.FONT_NAME_DUPLICATE);
        return BaseResponse.judge(mmFontService.updateById(font));
    }

    @ApiOperation(value = "删除字体")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        return BaseResponse.judge(mmFontService.removeByIds(Arrays.stream(ids.split(",")).map(Long::valueOf)
                .collect(Collectors.toList())));
    }

    @Deprecated
    @ApiOperation(value = "脚本,初始化mb等四个字段")
    @GetMapping("/initMb")
    public BaseResponse<StatusCode> initMb() {
        List<MmFont> result = new ArrayList<>();
        List<MmFont> list = mmFontService.list();
        list.forEach(mmFont -> {
                    mmFont.setPathMb(getPathSize(mmFont.getPath()));
                    mmFont.setBoldPathMb(getPathSize(mmFont.getBoldPath()));
                    mmFont.setItalicsPathMb(getPathSize(mmFont.getItalicsPath()));
                    mmFont.setBoldItalicsPathMb(getPathSize(mmFont.getBoldItalicsPath()));
                    result.add(mmFont);
                }
        );
        mmFontService.updateBatchById(result);
        return BaseResponse.success();
    }


    @Deprecated
    private Double getPathSize(String pathname) {
        byte[] bytes = new byte[0];
        try {
            log.info("url->" + pathname);
            if (StringUtil.isBlank(pathname)) {
                return 0D;
            }
            URL url = new URL(pathname);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(5 * 1000);
            //防止屏蔽程序抓取而返回403错误
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            bytes = IoUtil.readBytes(inputStream);
        } catch (Exception e) {
            log.error("初始化mb 异常pathname: {}", pathname);
            log.error(e.toString());
        }
        return ByteConvertUtil.b2Mb(bytes.length);
    }

}
