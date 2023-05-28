package com.makro.mall.product.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ProdIcon;
import com.makro.mall.product.pojo.entity.ProdIconPic;
import com.makro.mall.product.pojo.vo.ProdIconVO;
import com.makro.mall.product.service.ProdIconPicService;
import com.makro.mall.product.service.ProdIconService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/26
 **/
@Api(tags = "商品图标")
@RestController
@RequestMapping("/api/v1/product/icon")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class IconController {
    private final ProdIconService iconService;
    private final ProdIconPicService iconPicService;

    @ApiOperation(value = "商品图标分页")
    @GetMapping
    public BaseResponse<IPage<ProdIconVO>> list(@ApiParam("页码") Integer page,
                                                @ApiParam("每页数量") Integer limit,
                                                @ApiParam("图标") String name,
                                                @ApiParam("1：默认，0：其他") Integer isvalid) {
        ProdIcon icon = new ProdIcon();
        if (isvalid != null) {
            icon.setIsvalid(isvalid);
        }
        if (StrUtil.isNotBlank(name)) {
            icon.setName(name);
        }
        IPage<ProdIcon> result = iconService.list(new MakroPage<>(page, limit), icon);

        List<String> picIds = result.getRecords().stream().filter(p -> StrUtil.isNotEmpty(p.getPicid())).map(
                p -> p.getPicid()).collect(Collectors.toList());
        List<ProdIconPic> picList = CollectionUtil.isNotEmpty(picIds) ?
                iconPicService.getBaseMapper().selectList(
                        new LambdaQueryWrapper<ProdIconPic>().in(ProdIconPic::getId, picIds))
                : new ArrayList<>();
        Map<String, PicProperties<ProdIconPic>> iconPicVOMap = picList.stream().collect(
                Collectors.toMap(p -> String.valueOf(p.getId()), PicProperties::convert));
        IPage<ProdIconVO> resultList = result.convert(p -> {
            ProdIconVO vo = new ProdIconVO();
            BeanUtil.copyProperties(p, vo);
            vo.setPic(iconPicVOMap.get(p.getPicid()));
            return vo;
        });
        return BaseResponse.success(resultList);
    }

    @ApiOperation(value = "图片列表分页")
    @PostMapping("/pic/page")
    public BaseResponse<IPage<PicProperties<ProdIconPic>>> pageList(@RequestBody SortPageRequest<ProdIconPic> request) {
        String sortSql = request.getSortSql();
        ProdIconPic req = request.getReq();
        LambdaQueryWrapper<ProdIconPic> queryWrapper = new LambdaQueryWrapper<ProdIconPic>()
                .eq(ProdIconPic::getDeleted, 0L)
                .eq(req != null && req.getDefaulted() != null, ProdIconPic::getDefaulted, req.getDefaulted())
                .eq(req != null && req.getIconid() != null, ProdIconPic::getIconid, req.getIconid())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<ProdIconPic> result = iconPicService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        IPage<PicProperties<ProdIconPic>> resultVo = result.convert(PicProperties::convert);
        return BaseResponse.success(resultVo);
    }

    @ApiOperation(value = "新增商品图标")
    @PostMapping(value = "/add")
    public BaseResponse add(@RequestBody ProdIcon icon) {
        ProdIcon iconExist = new ProdIcon();
        String name = StrUtil.trim(icon.getName());
        String nameUpper = name != null ? name.toUpperCase() : "";
        iconExist.setName(nameUpper);
        // 判断name是否存在，不存在则新增，存在则否。
        IPage<ProdIcon> result = iconService.list(new MakroPage<>(1, 1), iconExist);
        Long total = result.getTotal();
        if (total == 0) {
            icon.setId(IdUtil.simpleUUID());
            icon.setName(nameUpper);
            icon.setCreator(JwtUtils.getUsername());
            icon.setIsvalid(1);
            iconService.save(icon);
            return BaseResponse.success(icon);
        } else {
            return BaseResponse.judge(false, "Duplicate Record.");
        }
    }

    @ApiOperation(value = "商品图标详情")
    @GetMapping("/{id}")
    public BaseResponse details(@PathVariable String id) {
        ProdIcon icon = iconService.getById(id);
        return BaseResponse.success(icon);
    }

    @ApiOperation(value = "更新商品图标")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public BaseResponse update(@PathVariable String id, @RequestBody ProdIcon icon) {
        Boolean result = iconService.update(id, icon);
        return BaseResponse.judge(result);
    }

    @ApiOperation(value = "删除商品图标")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        Boolean status = iconService.removeByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        return BaseResponse.judge(status);
    }

    @ApiOperation(value = "商品图标下拉列表")
    @RequestMapping(value = "/getIconNames", method = RequestMethod.GET)
    public BaseResponse geticonType() {
        List<String> list = iconService.getIconNames();
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "根据名称获取图标及图片")
    @RequestMapping(value = "/getOneByName", method = RequestMethod.GET)
    public BaseResponse getIconWithPicture(String name) {
        if (StrUtil.isNotBlank(name)) {
            name = name.toUpperCase(Locale.ROOT);
        } else {
            return BaseResponse.error("No icon name.");
        }
        LambdaQueryWrapper<ProdIcon> qw = new LambdaQueryWrapper<ProdIcon>()
                .eq(StrUtil.isNotBlank(name), ProdIcon::getName, name);
        ProdIcon icon = iconService.getOne(qw);
        ProdIconVO iconvo = null;
        ProdIconPic iconPic = null;
        LambdaQueryWrapper<ProdIcon> iconQW = new LambdaQueryWrapper<ProdIcon>()
                .eq(StrUtil.isNotBlank(name), ProdIcon::getName, name);
        icon = iconService.getOne(iconQW);
        if (icon != null) {
            iconvo = new ProdIconVO();
            BeanUtil.copyProperties(icon, iconvo);
            String iconPicId = icon.getPicid();
            if (iconPicId != null) {
                iconPic = iconPicService.getById(iconPicId);
                iconvo.setPic(PicProperties.convert(iconPic));
            }
        }
        return BaseResponse.success(iconvo);
    }

    @ApiOperation(value = "新增图标图片")
    @PostMapping(value = "/pic/add")
    @Transactional
    public BaseResponse add(@RequestBody ProdIconPic pic) {
        pic.setId(IdUtil.simpleUUID());
        String iconId = pic.getIconid();
        boolean isSuccess = iconPicService.save(pic);
        if (isSuccess) {
            if (Objects.equals(1L, pic.getDefaulted())) {
                iconPicService.update(new LambdaUpdateWrapper<ProdIconPic>().eq(ProdIconPic::getIconid, pic.getIconid())
                        .ne(ProdIconPic::getId, pic.getId())
                        .set(ProdIconPic::getDefaulted, 0));
                iconService.update(new LambdaUpdateWrapper<ProdIcon>()
                        .eq(ProdIcon::getId, iconId)
                        .set(ProdIcon::getPicid, pic.getId()));
                log.info("新增图片成功{},并重置其他图片为非默认", pic.getId());
            }
            return BaseResponse.success(pic);
        } else {
            return BaseResponse.error("Add Picture Fail.");
        }
    }

    @ApiOperation(value = "详情")
    @GetMapping("/pic/{id}")
    public BaseResponse<PicProperties<ProdIconPic>> detail(@PathVariable String id) {
        return BaseResponse.success(PicProperties.convert(iconPicService.getById(id)));
    }

    @ApiOperation(value = "修改图标图片")
    @PutMapping(value = "/pic/{id}")
    public BaseResponse update(@PathVariable String id, @RequestBody ProdIconPic pic) {
        return BaseResponse.judge(iconService.updatePic(id, pic));
    }
}
