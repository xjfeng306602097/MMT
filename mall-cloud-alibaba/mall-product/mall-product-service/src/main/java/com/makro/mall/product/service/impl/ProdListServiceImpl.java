package com.makro.mall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.api.MmActivityExFeignClient;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.ProductStatusCode;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.product.mapper.ProdListMapper;
import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.dto.ProdInfo;
import com.makro.mall.product.pojo.entity.*;
import com.makro.mall.product.pojo.vo.ProdIconVO;
import com.makro.mall.product.pojo.vo.ProdPicVO;
import com.makro.mall.product.pojo.vo.ProductVO;
import com.makro.mall.product.pojo.vo.SimpleProductVO;
import com.makro.mall.product.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Thierry.Zhuang
 * @description 针对表【PROD_LIST(添加或导入模板里商品设计相关数据，MM商品明细的来源)】的数据库操作Service实现
 * @createDate 2022-04-14 13:08:41
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProdListServiceImpl extends ServiceImpl<ProdListMapper, ProdList>
        implements ProdListService {

    private final ItemCodeSegmentService itemCodeSegmentService;
    private final ProdIconService iconService;
    private final ProdIconPicService iconPicService;
    private final ProdStorageService storageService;
    private final ProdPicService picService;

    private final MmActivityExFeignClient mmActivityExFeignClient;

    @Override
    public void saveBatchProdList(List<ProdList> list) {
        this.saveBatch(list);
    }

    @Override
    public List<ProdList> linkItemList(String itemCode, String mmCode, Integer isvalid) {
        //防OOM
        if (StrUtil.isEmpty(mmCode) || StrUtil.isEmpty(itemCode)) {
            log.error("没有传mmCode||itemCode调用linkItemList");
            return new ArrayList<>();
        }
        return this.baseMapper.linkItemList(itemCode, mmCode, isvalid);
    }

    @Override
    public List<String> getPages(String mmCode) {
        return this.baseMapper.getPages(mmCode);
    }

    @Override
    public IPage<ProductVO> selectList(Integer page, Integer limit, String itemcode, String namethai, String mmCode, String infoid, String channelType, Integer isvalid, Integer mmpage, Integer mmsort, String nameen, Long segmentId, List<String> join, Long productId) {
        List<String> itemCodes = null;
        //查出segmentId下的ProdListId
        if (ObjectUtil.isNotNull(segmentId)) {
            itemCodes = itemCodeSegmentService.getItemCodesBySegmentId(segmentId);
        }
        LambdaQueryWrapper<ProdList> qw = new LambdaQueryWrapper<ProdList>()
                .eq(StrUtil.isNotBlank(itemcode), ProdList::getUrlparam, itemcode)
                .like(StrUtil.isNotBlank(namethai), ProdList::getNamethai, namethai)
                .eq(StrUtil.isNotBlank(mmCode), ProdList::getMmCode, mmCode)
                .eq(StrUtil.isNotBlank(infoid), ProdList::getInfoid, infoid)
                .eq(StrUtil.isNotBlank(channelType), ProdList::getChannelType, channelType)
                .eq(StrUtil.isNotBlank(nameen), ProdList::getNameen, nameen)
                .eq(mmpage != null, ProdList::getPage, mmpage)
                .eq(mmsort != null, ProdList::getSort, mmsort)
                .eq(isvalid != null, ProdList::getIsvalid, isvalid)
                .eq(productId != null, ProdList::getProductId, productId)
                .in(ObjectUtil.isNotNull(segmentId), ProdList::getUrlparam, itemCodes)
                .orderByAsc(mmpage != null, ProdList::getPage)
                .orderByAsc(mmsort != null, ProdList::getSort);
        IPage<ProdList> prodListIPage = getBaseMapper().selectPage(new MakroPage<>(page, limit), qw);

        //转换对象
        IPage<ProductVO> resultList = new MakroPage<>(page, limit);
        resultList.setTotal(prodListIPage.getTotal());
        resultList.setRecords(prodListIPage.getRecords().stream().map(x -> getProductVO(x, join)).collect(Collectors.toList()));
        return resultList;
    }


    /**
     * @param join     里面可以出现的值：icons、linkItems、pic（除了info之外的其他对象）默认不传参查全部，为空只查info，其他的包含什么返回什么
     * @param dataList 快速MM未存表,只在内存内查找
     */
    @Override
    public ProductVO getProductVO(ProdList data, List<String> join, List<ProdList> dataList) {
        //初始化
        if (join == null) {
            join = List.of("icons", "linkItems", "pic");
        }
        ProductVO vo = new ProductVO();
        PicProperties<ProdPicVO> picvo = new PicProperties<>();

        //商品图片
        if (join.contains("pic")) {
            vo.setPic(getPic(data.getPicid(), picvo));
        }

        //图标
        if (join.contains("icons")) {
            vo.setIcons(getIcons(data));
        }

        // 关联商品
        if (join.contains("linkItems")) {
            vo.setLinkItems(getLinkItems(data, dataList));
        }

        //info
        ProdInfo info = new ProdInfo();
        BeanUtil.copyProperties(data, info);
        info.setItemcode(data.getUrlparam());
        vo.setInfo(info);

        return vo;
    }

    @Override
    public ProductVO getProductVO(ProdList prodList, List<String> join) {
        return getProductVO(prodList, join, null);
    }

    @Override
    public ProductVO getProductVO(ProdList prodList) {
        return getProductVO(prodList, null, null);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public Boolean add(String mmCode, ProdList data) {
        data.setId(IdUtil.simpleUUID());
        data.setMmCode(mmCode);
        data.setIsvalid(1);
        data.setCreator(JwtUtils.getUsername());
        save(data);
        ProdStorage storage = new ProdStorage();
        BeanUtil.copyProperties(data, storage);
        String storageId = Optional.ofNullable(data.getUrlparam()).orElse(IdUtil.simpleUUID());
        storage.setId(storageId);
        storage.setLastmodifydataid(data.getId());
        return storageService.saveOrUpdateProdStorage(storage);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public Boolean updateForProd(String id, ProdList data) {
        //从PROD_PIC 相同的ITEM_CODE中获取LIST 查询是否有DEFAULTED字段,若无则选择本次为默认
        if (data.getPicid() != null) {
            ProdList prodList = getById(id);
            String itemCode = prodList.getUrlparam();
            if (itemCode != null) {
                ProdPic pic = picService.getOne(new LambdaQueryWrapper<ProdPic>().eq(ProdPic::getItemCode, itemCode).eq(ProdPic::getDeleted, 0L).eq(ProdPic::getDefaulted, 1L));
                if (pic == null) {
                    ProdPic prodPic = new ProdPic();
                    prodPic.setId(Long.valueOf(data.getPicid()));
                    prodPic.setDefaulted(1L);
                    picService.updateById(prodPic);
                }
            }
        }

        data.setId(id);
        updateById(data);
        data = getById(id);


        ProdStorage storage = new ProdStorage();
        BeanUtil.copyProperties(data, storage);
        String storageId = Optional.ofNullable(data.getUrlparam()).orElse(IdUtil.simpleUUID());
        storage.setId(storageId);
        storage.setLastmodifydataid(data.getId());
        return storageService.saveOrUpdateProdStorage(storage);
    }

    @Override
    public List<ProductVO> listByIdsForProd(String ids) {
        List<ProdList> prodLists = listByIds(Arrays.asList(ids.split(",")));
        return prodLists.parallelStream().map(d -> getProductVO(d)).collect(Collectors.toList());
    }

    @Override
    public IPage<MmActivity> getMmActivityByItemCode(String itemCode, Long page, Long limit) {
        LambdaQueryWrapper<ProdList> qw = new LambdaQueryWrapper<ProdList>()
                .eq(StrUtil.isNotBlank(itemCode), ProdList::getUrlparam, itemCode);
        List<ProdList> dataList = list(qw);
        MakroPage<MmActivity> mmList;
        if (CollectionUtil.isNotEmpty(dataList)) {
            String mmCodes = dataList.stream().map(ProdList::getMmCode).distinct().
                    collect(Collectors.joining("','", "['", "']"));
            if (StrUtil.isNotBlank(mmCodes)) {
                mmList = mmActivityExFeignClient.getByCodes(JSON.parseArray(mmCodes), page, limit).getData();
            } else {
                mmList = new MakroPage<>(page, limit);
            }
        } else {
            mmList = new MakroPage<>(page, limit);
        }
        return mmList;
    }

    @Override
    public BaseResponse<String> getMmCodeByItemCode(String itemCode) {
        List<ProdList> list = list(new LambdaQueryWrapper<ProdList>()
                .eq(StrUtil.isNotBlank(itemCode), ProdList::getUrlparam, itemCode));
        if (CollUtil.isEmpty(list)) {
            return BaseResponse.success("", "ItemCode找不到对应MmCode");
        }
        String mmCodes = list.stream().map(ProdList::getMmCode).distinct().collect(Collectors.joining("','", "('", "')"));
        return BaseResponse.success(mmCodes, "请求成功");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean parentIdScript() {
        //删除已失效的textthai
        remove(new LambdaQueryWrapper<ProdList>().eq(ProdList::getIsvalid, 0));
        //查找出所有textthai
        List<ProdList> allProdLists = list();
        //根据mmcode进行分类
        HashMap<String, List<ProdList>> map = new HashMap<>(allProdLists.size());
        allProdLists.forEach(x -> {
            if (!map.containsKey(x.getMmCode())) {
                //如果不存在则初始化
                map.put(x.getMmCode(), new ArrayList<>());
            }
            map.get(x.getMmCode()).add(x);
        });

        //遍历map填充每个MM的父ID
        map.forEach((x, y) -> y.forEach(prodList -> {
            //存进后 找出关联商品填充其parentId
            if (StrUtil.isNotBlank(prodList.getParentCode())) {
                //从商品列表查出
                for (ProdList father : y) {
                    if (ObjectUtil.equals(father.getUrlparam(), prodList.getParentCode())) {
                        prodList.setParentId(father.getId());
                        return;
                    }
                }
            }
        }));

        //去除没有ParentId的数据
        map.forEach((x, y) -> {
            List<ProdList> filterMap = y.stream().filter(prodList -> StrUtil.isNotBlank(prodList.getParentCode())).collect(Collectors.toList());
            updateBatchById(filterMap);
        });

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateValid(String id, Integer valid) {
        //关联商品不可进行恢复
        ProdList prodList = getById(id);
        Assert.isTrue(StrUtil.isEmpty(prodList.getParentId()), ProductStatusCode.ASSOCIATED_PRODUCTS_CANNOT_BE_DELETED);
        if (valid == 1) {
            List<ProdList> list = list(new LambdaQueryWrapper<ProdList>().eq(ProdList::getIsvalid, 1).eq(ProdList::getMmCode, prodList.getMmCode()));
            for (ProdList dbProdList : list) {
                //把该MM有效的查出来判断是否Item code重复 page sort重复
                Assert.notTrue(StrUtil.equals(dbProdList.getUrlparam(), prodList.getUrlparam()), ProductStatusCode.ITEM_CODE_CANNOT_BE_DUPLICATED);
                Assert.notTrue(ObjectUtil.equals(dbProdList.getPage(), prodList.getPage()) && ObjectUtil.equals(dbProdList.getSort(), prodList.getSort()), ProductStatusCode.THE_PAGE_AND_SORT_OF_THE_CURRENT_PRODUCT_ALREADY_EXIST);
            }
        }
        //更新主商品
        prodList.setIsvalid(valid);
        updateById(prodList);

        //更新关联商品
        update(new LambdaUpdateWrapper<ProdList>().isNull(ProdList::getChannelType).isNotNull(ProdList::getParentId).eq(ProdList::getParentId, id).set(ProdList::getIsvalid, valid));
        return true;
    }

    @Override
    public Boolean updatebyItemCode(String mmCode, String itemCode, String parentCode, ProdList data) {
        //从PROD_PIC 相同的ITEM_CODE中获取LIST 查询是否有DEFAULTED字段,若无则选择本次为默认
        ProdList prodList = getOne(new LambdaQueryWrapper<ProdList>().eq(ProdList::getMmCode, mmCode).eq(ProdList::getUrlparam, itemCode).eq(StrUtil.isNotBlank(parentCode), ProdList::getParentCode, parentCode));
        String id = prodList.getId();
        if (data.getPicid() != null) {
            ProdPic pic = picService.getOne(new LambdaQueryWrapper<ProdPic>().eq(ProdPic::getItemCode, itemCode).eq(ProdPic::getDeleted, 0L).eq(ProdPic::getDefaulted, 1L));
            if (pic == null) {
                ProdPic prodPic = new ProdPic();
                prodPic.setId(Long.valueOf(data.getPicid()));
                prodPic.setDefaulted(1L);
                picService.updateById(prodPic);
            }
        }

        data.setId(id);
        updateById(data);
        data = getById(id);


        ProdStorage storage = new ProdStorage();
        BeanUtil.copyProperties(data, storage);
        String storageId = Optional.ofNullable(data.getUrlparam()).orElse(IdUtil.simpleUUID());
        storage.setId(storageId);
        storage.setLastmodifydataid(data.getId());
        return storageService.saveOrUpdateProdStorage(storage);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAllForProd(String id, ProdList data) {
        //从PROD_PIC 相同的ITEM_CODE中获取LIST 查询是否有DEFAULTED字段,若无则选择本次为默认
        if (data.getPicid() != null) {
            ProdList prodList = getById(id);
            String itemCode = prodList.getUrlparam();
            if (itemCode != null) {
                ProdPic pic = picService.getOne(new LambdaQueryWrapper<ProdPic>().eq(ProdPic::getItemCode, itemCode).eq(ProdPic::getDeleted, 0L).eq(ProdPic::getDefaulted, 1L));
                if (pic == null) {
                    ProdPic prodPic = new ProdPic();
                    prodPic.setId(Long.valueOf(data.getPicid()));
                    prodPic.setDefaulted(1L);
                    picService.updateById(prodPic);
                }
            }
        }

        data.setId(id);
        getBaseMapper().updateAll(data);
        data = getById(id);


        ProdStorage storage = new ProdStorage();
        BeanUtil.copyProperties(data, storage);
        String storageId = Optional.ofNullable(data.getUrlparam()).orElse(IdUtil.simpleUUID());
        storage.setId(storageId);
        storage.setLastmodifydataid(data.getId());
        return storageService.saveOrUpdateProdStorage(storage);
    }

    /***
     *
     * @param dataList 快速MM未存表,只在内存内查找
     */
    @NotNull
    private List<SimpleProductVO> getLinkItems(ProdList data, List<ProdList> dataList) {
        List<SimpleProductVO> linkItems = new ArrayList<>();
        List<ProdList> linkItemList;
        if (ObjectUtil.isNull(data.getMmCode())) {
            linkItemList = getLinkItemListFromProdList(data.getUrlparam(), dataList);
        } else {
            linkItemList = linkItemList(data.getUrlparam(), data.getMmCode(), 1);

        }

        //将拥有giftCode的数据过滤出来批量查找
        Set<String> giftCodes = linkItemList.stream().map(ProdList::getUrlparam).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        List<ProdStorage> prodStorages = new ArrayList<>();
        if (CollUtil.isNotEmpty(giftCodes)) {
            prodStorages = storageService.listByIds(giftCodes);
        }

        //将拥有giftPicId的数据过滤出来批量查找
        Set<String> giftPicIds = linkItemList.stream().map(ProdList::getPicid).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        List<ProdPic> prodPics = new ArrayList<>();
        if (CollUtil.isNotEmpty(giftPicIds)) {
            prodPics = picService.listByIds(giftPicIds);
        }

        int i = 0;
        for (ProdList giftData : linkItemList) {
            String giftCode = giftData.getUrlparam();
            if (StrUtil.isNotBlank(giftCode)) {
                ProdStorage gift = prodStorages.stream().filter(x -> StrUtil.equals(giftCode, x.getId())).findFirst().orElse(null);
                SimpleProductVO giftVO = new SimpleProductVO();
                giftVO.setInfo(gift);
                i = i + 1;
                giftVO.setSort(i);
                String giftPicId = giftData.getPicid();
                if (giftPicId != null) {
                    ProdPic giftPic = prodPics.stream().filter(x -> StrUtil.equals(giftPicId, x.getId().toString())).findFirst().orElse(null);
                    giftVO.setPic(PicProperties.convert(giftPic));
                }
                linkItems.add(giftVO);
            }
        }
        return linkItems;
    }

    private List<ProdList> getLinkItemListFromProdList(String urlparam, List<ProdList> data) {
        return data.stream().filter(x -> StrUtil.equals(x.getParentCode(), urlparam)).collect(Collectors.toList());
    }

    private List<ProdIconVO> getIcons(ProdList data) {
        List<ProdIconVO> iconvos = new ArrayList<>();
        setIcon(StrUtil.trim(data.getIcon1()), "Icon1", iconvos);
        setIcon(StrUtil.trim(data.getIcon2()), "Icon2", iconvos);
        setIcon(StrUtil.trim(data.getIcon3()), "Icon3", iconvos);
        return iconvos;
    }

    private void setIcon(String condition, String icon, List<ProdIconVO> iconvos) {
        if (!StringUtils.isEmpty(condition)) {
            ProdIcon prodIcon = iconService.getOne(new LambdaQueryWrapper<ProdIcon>().eq(ProdIcon::getName, icon));
            ProdIconVO iconvo = new ProdIconVO();
            if (prodIcon != null) {
                BeanUtil.copyProperties(prodIcon, iconvo);
                String iconPicId = prodIcon.getPicid();
                if (iconPicId != null) {
                    ProdIconPic iconPic = iconPicService.getById(iconPicId);
                    iconvo.setPic(PicProperties.convert(iconPic));
                }
            }
            iconvo.setIconIndex(icon);
            iconvos.add(iconvo);
        }
    }

    private PicProperties<ProdPicVO> getPic(String picId, PicProperties<ProdPicVO> picvo) {
        if (picId != null) {
            ProdPic pic = picService.getById(picId);
            picvo = picvo.convert(pic);
        }
        return picvo;
    }
}




