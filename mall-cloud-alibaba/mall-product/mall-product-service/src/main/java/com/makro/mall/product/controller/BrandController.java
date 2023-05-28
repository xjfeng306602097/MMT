package com.makro.mall.product.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.common.model.*;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ProdBrand;
import com.makro.mall.product.pojo.entity.ProdBrandPic;
import com.makro.mall.product.pojo.vo.ProBrandVO;
import com.makro.mall.product.service.ProdBrandPicService;
import com.makro.mall.product.service.ProdBrandService;
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
@Api(tags = "商品品牌")
@RestController
@RequestMapping("/api/v1/product/brand")
@RequiredArgsConstructor
@Slf4j
@RefreshScope
public class BrandController {
    private final ProdBrandService brandService;
    private final ProdBrandPicService brandPicService;

    @ApiOperation(value = "商品品牌分页")
    @GetMapping
    public BaseResponse<IPage<ProBrandVO>> list(@ApiParam("页码") Integer page,
                                                @ApiParam("每页数量") Integer limit,
                                                @ApiParam("品牌") String name,
                                                @ApiParam("1：默认，0：其他") Integer isvalid) {
        ProdBrand brand = new ProdBrand();
        if (isvalid != null) {
            brand.setIsvalid(isvalid);
        }
        if (StrUtil.isNotBlank(name)) {
            brand.setName(name);
        }
        IPage<ProdBrand> result = brandService.list(new MakroPage<>(page, limit), brand);

        List<String> picIds = result.getRecords().stream().filter(p -> StrUtil.isNotEmpty(p.getPicid())).map(
                p -> p.getPicid()).collect(Collectors.toList());
        List<ProdBrandPic> picList = CollectionUtil.isNotEmpty(picIds) ?
                brandPicService.getBaseMapper().selectList(
                        new LambdaQueryWrapper<ProdBrandPic>().in(ProdBrandPic::getId, picIds))
                : new ArrayList<>();
        Map<String, PicProperties<ProdBrandPic>> brandPicVOMap = picList.stream().collect(
                Collectors.toMap(p -> String.valueOf(p.getId()), PicProperties::convert));
        IPage<ProBrandVO> resultList = result.convert(p -> {
            ProBrandVO vo = new ProBrandVO();
            BeanUtil.copyProperties(p, vo);
            vo.setPic(brandPicVOMap.get(p.getPicid()));
            return vo;
        });
        return BaseResponse.success(resultList);
    }


    @ApiOperation(value = "图片列表分页")
    @PostMapping("/pic/page")
    public BaseResponse<IPage<PicProperties<ProdBrandPic>>> pageList(@RequestBody SortPageRequest<ProdBrandPic> request) {
        String sortSql = request.getSortSql();
        ProdBrandPic req = request.getReq();
        LambdaQueryWrapper<ProdBrandPic> queryWrapper = new LambdaQueryWrapper<ProdBrandPic>()
                .eq(ProdBrandPic::getDeleted, 0L)
                .eq(req != null && req.getDefaulted() != null, ProdBrandPic::getDefaulted, req.getDefaulted())
                .eq(req != null && req.getBrandid() != null, ProdBrandPic::getBrandid, req.getBrandid())
                .last(StrUtil.isNotEmpty(sortSql), sortSql);
        IPage<ProdBrandPic> result = brandPicService.page(new MakroPage<>(request.getPage(), request.getLimit()), queryWrapper);
        IPage<PicProperties<ProdBrandPic>> resultVo = result.convert(PicProperties::convert);
        return BaseResponse.success(resultVo);
    }

    @ApiOperation(value = "新增商品品牌")
    @PostMapping(value = "/add")
    public BaseResponse add(@RequestBody ProdBrand brand) {
        ProdBrand brandExist = new ProdBrand();
        // TODO 测试泰文
        String name = brand.getName();
        String nameUpper = name != null ? name.toUpperCase() : "";
        brandExist.setName(nameUpper);
        // 判断name是否存在，不存在则新增，存在则否。
        IPage<ProdBrand> result = brandService.list(new MakroPage<>(1, 1), brandExist);
        Long total = result.getTotal();
        if (total == 0) {
            brand.setId(IdUtil.simpleUUID());
            brand.setName(nameUpper);
            brand.setCreator(JwtUtils.getUsername());
            brand.setIsvalid(1);
            brandService.save(brand);
            return BaseResponse.success(brand);
        } else {
            return BaseResponse.judge(false, "Duplicate Record.");
        }
    }

    @ApiOperation(value = "商品品牌详情")
    @GetMapping("/{id}")
    public BaseResponse details(@PathVariable String id) {
        ProdBrand brand = brandService.getById(id);
        return BaseResponse.success(brand);
    }

    @ApiOperation(value = "更新商品品牌")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PUT)
    public BaseResponse update(@PathVariable String id, @RequestBody ProdBrand brand) {
        return BaseResponse.judge(brandService.update(id, brand));
    }

    @ApiOperation(value = "删除商品品牌")
    @DeleteMapping("/{ids}")
    public BaseResponse delete(@PathVariable String ids) {
        Boolean status = brandService.removeByIds(Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        return BaseResponse.judge(status);
    }

    @ApiOperation(value = "商品品牌下拉列表")
    @RequestMapping(value = "/getBrandNames", method = RequestMethod.GET)
    public BaseResponse getbrandType() {
        List<String> list = brandService.getBrandNames();
        return BaseResponse.success(list);
    }

    @ApiOperation(value = "根据名称获取品牌及图片")
    @RequestMapping(value = "/getOneByName", method = RequestMethod.GET)
    public BaseResponse getbrandWithPicture(String name) {
        if (StrUtil.isNotBlank(name)) {
            name = name.toUpperCase(Locale.ROOT);
        } else {
            return BaseResponse.error("No brand name.");
        }
        LambdaQueryWrapper<ProdBrand> qw = new LambdaQueryWrapper<ProdBrand>()
                .eq(StrUtil.isNotBlank(name), ProdBrand::getName, name);
        ProdBrand brand = brandService.getOne(qw);
        ProBrandVO brandvo = null;
        ProdBrandPic brandPic = null;
        LambdaQueryWrapper<ProdBrand> brandQW = new LambdaQueryWrapper<ProdBrand>()
                .eq(StrUtil.isNotBlank(name), ProdBrand::getName, name);
        brand = brandService.getOne(brandQW);
        if (brand != null) {
            brandvo = new ProBrandVO();
            BeanUtil.copyProperties(brand, brandvo);
            String brandPicId = brand.getPicid();
            if (brandPicId != null) {
                brandPic = brandPicService.getById(brandPicId);
                brandvo.setPic(PicProperties.convert(brandPic));
            }
        }
        return BaseResponse.success(brandvo);
    }

    @ApiOperation(value = "新增品牌图片")
    @PostMapping(value = "/pic/add")
    @Transactional
    public BaseResponse add(@RequestBody ProdBrandPic pic) {
        pic.setId(IdUtil.simpleUUID());
        String brandId = pic.getBrandid();
        boolean isSuccess = brandPicService.save(pic);
        if (isSuccess) {
            if (Objects.equals(pic.getDefaulted(), 1L)) {
                brandPicService.update(new LambdaUpdateWrapper<ProdBrandPic>().eq(ProdBrandPic::getBrandid, pic.getBrandid())
                        .ne(ProdBrandPic::getId, pic.getId())
                        .set(ProdBrandPic::getDefaulted, 0));
                brandService.update(new LambdaUpdateWrapper<ProdBrand>()
                        .eq(ProdBrand::getId, brandId)
                        .set(ProdBrand::getPicid, pic.getId()));
                log.info("新增图片成功{},并重置其他图片为非默认", pic.getId());
            }
            return BaseResponse.success(pic);
        } else {
            return BaseResponse.error("Add Picture Fail.");
        }
    }

    @ApiOperation(value = "详情")
    @GetMapping("/pic/{id}")
    public BaseResponse<PicProperties<ProdBrandPic>> detail(@PathVariable String id) {
        return BaseResponse.success(PicProperties.convert(brandPicService.getById(id)));
    }

    @ApiOperation(value = "修改品牌图片")
    @PutMapping(value = "/pic/{id}")
    @Transactional
    public BaseResponse update(@PathVariable String id, @RequestBody ProdBrandPic pic) {
        pic.setId(id);
        String brandId = pic.getBrandid();
        pic.setDefaulted(1L);
        if (pic.getDefaulted() != null) {
            //brandId不能为空
            Assert.isTrue(Objects.nonNull(brandId), ProductStatusCode.BRANDID_IS_EMPTY);

            List<ProdBrandPic> picList = brandPicService.list(new LambdaQueryWrapper<ProdBrandPic>().eq(ProdBrandPic::getBrandid, brandId));
            if (Objects.equals(pic.getDefaulted(), 1L)) {
                // 调为默认
                for (ProdBrandPic prodBrandPic : picList) {
                    if (Objects.equals(prodBrandPic.getDefaulted(), 1L) && !Objects.equals(prodBrandPic.getId(), id)) {
                        prodBrandPic.setDefaulted(0L);
                        // 直接跳出，只允许一条记录更新
                        brandPicService.updateById(prodBrandPic);
                    }
                }
                brandService.update(new LambdaUpdateWrapper<ProdBrand>()
                        .eq(ProdBrand::getId, brandId)
                        .set(ProdBrand::getPicid, pic.getId()));
            } else {
                // 检查是否存在默认图片
                Assert.isTrue(picList.stream().anyMatch(p -> Objects.equals(p.getDefaulted(), 1L) && !p.getId()
                        .equals(pic.getId())), "Brand.picture.defaulted.required");
            }
        }
        return BaseResponse.judge(brandPicService.updateById(pic));

    }

}
