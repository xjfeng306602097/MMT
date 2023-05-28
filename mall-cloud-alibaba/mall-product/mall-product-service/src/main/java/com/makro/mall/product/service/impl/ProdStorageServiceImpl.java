package com.makro.mall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.api.MmSegmentFeignClient;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.ProductStatusCode;
import com.makro.mall.product.Listener.ProdStorageExcelListener;
import com.makro.mall.product.mapper.ProdStorageMapper;
import com.makro.mall.product.pojo.dto.ItemCodeSegmentDelDTO;
import com.makro.mall.product.pojo.dto.PicProperties;
import com.makro.mall.product.pojo.entity.ItemCodeSegment;
import com.makro.mall.product.pojo.entity.ProdIconPic;
import com.makro.mall.product.pojo.entity.ProdPic;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.vo.ProdStoragePageVO;
import com.makro.mall.product.pojo.vo.ProdStorageVO;
import com.makro.mall.product.service.ItemCodeSegmentService;
import com.makro.mall.product.service.ProdPicService;
import com.makro.mall.product.service.ProdStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProdStorageServiceImpl extends ServiceImpl<ProdStorageMapper, ProdStorage>
        implements ProdStorageService {

    private final ItemCodeSegmentService itemCodeSegmentService;
    private final MmSegmentFeignClient mmSegmentFeignClient;
    private final ProdPicService prodPicService;

    @Override
    public Boolean saveOrUpdateProdStorage(ProdStorage storage) {
        return this.saveOrUpdate(storage);
    }

    @Override
    public Boolean batchSaveOrUpdateProdStorage(Collection<ProdStorage> list) {
        return this.saveOrUpdateBatch(list);
    }

    @Override
    public IPage<ProdStoragePageVO> list(Page<ProdStorage> page, ProdStorage storage, String segmentId) {
        List<ProdStorage> list = this.baseMapper.list(page, storage, segmentId);
        page.setRecords(list);
        List<String> prodStorageIds = list.stream().map(ProdStorage::getId).collect(Collectors.toList());

        List<ProdPic> picList = CollectionUtil.isNotEmpty(prodStorageIds)
                ? prodPicService.list(new LambdaQueryWrapper<ProdPic>().in(ProdPic::getItemCode, prodStorageIds).eq(ProdPic::getDefaulted,1L))
                : new ArrayList<>();

        Map<String, PicProperties<ProdIconPic>> iconPicMap = picList.stream().collect(Collectors.toMap(p -> String.valueOf(p.getItemCode()), PicProperties::convert));

        return page.convert(p -> {
            ProdStoragePageVO vo = new ProdStoragePageVO();
            BeanUtil.copyProperties(p, vo);
            vo.setPic(iconPicMap.get(p.getItemcode()));
            return vo;
        });
    }

    @Override
    public List<ProdStorageVO> parseExcel(MultipartFile file, String segmentName) {
        ProdStorageExcelListener excelListener = new ProdStorageExcelListener(this, segmentName);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes())) {
            EasyExcel.read(inputStream, ProdStorageVO.class, excelListener).sheet().doRead();
        } catch (IOException e) {
            //异常输出
            StringBuilder sb = new StringBuilder();
            Arrays.stream(e.getStackTrace()).forEach(x -> sb.append("\r\n\t").append(x));
            log.error("商品解析失败 IOException:{}", sb.length() == 0 ? null : sb.toString());
            return Collections.emptyList();
        }
        return excelListener.getList();
    }

    @Override
    public void storageValidated(List<ProdStorageVO> prodStorageVO) {
        prodStorageVO.forEach(x -> Assert.isTrue(StrUtil.isNotBlank(x.getItemcode()), ProductStatusCode.ITEM_CODE_IS_EMPTY));
    }

    @Override
    public void createOrUpdateBatch(List<ProdStorageVO> prodStorageVO) {
        //整理ProdStorage对象
        prodStorageHandle(prodStorageVO);
        //整理Segment对象
        segmentHandle(prodStorageVO);
    }

    @Override
    public Boolean delItemCodeSegment(ItemCodeSegmentDelDTO dto) {
        if (CollectionUtil.isNotEmpty(dto.getSegmentId())){
            itemCodeSegmentService.remove(new LambdaQueryWrapper<ItemCodeSegment>().in(ItemCodeSegment::getSegmentId,dto.getSegmentId()));
        }

        if (CollectionUtil.isNotEmpty(dto.getItemCode())){
            itemCodeSegmentService.remove(new LambdaQueryWrapper<ItemCodeSegment>().in(ItemCodeSegment::getItemCode,dto.getItemCode()));
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(String id) {
        ProdStorage storage = getById(id);
        storage.setIsvalid(0);
        return updateById(storage) && delItemCodeSegment(new ItemCodeSegmentDelDTO(List.of(id),null));
    }

    private void segmentHandle(List<ProdStorageVO> prodStorageVO) {
        //1.先查找所有商品与segment现有关系,建立map
        Set<String> itemCodes = prodStorageVO.stream().map(ProdStorageVO::getItemcode).collect(Collectors.toSet());
        List<ItemCodeSegment> itemCodeSegments = itemCodeSegmentService.list(new LambdaQueryWrapper<ItemCodeSegment>().in(ItemCodeSegment::getItemCode, itemCodes));

        //2.构建预插入List
        List<ItemCodeSegment> insertItemCodeSegments = new LinkedList<>();
        //遍历每一个segment
        prodStorageVO.forEach(x -> {
            Long segmentId = mmSegmentFeignClient.getIdIfNullCreateThat(x.getSegmentName()).getData();
            //排除原来的
            insertItemCodeSegments.add(new ItemCodeSegment(x.getId(), segmentId));
        });

        //3.去重
        List<ItemCodeSegment> collect = insertItemCodeSegments.stream().filter(x -> !itemCodeSegments.contains(x)).collect(Collectors.toList());

        //4.存储中间表
        itemCodeSegmentService.saveBatch(collect);
    }

    private void prodStorageHandle(List<ProdStorageVO> prodStorageVO) {
        batchSaveOrUpdateProdStorage(prodStorageVO.stream().map(x -> {
            ProdStorage prodStorage = new ProdStorage();
            BeanUtil.copyProperties(x, prodStorage);
            prodStorage.setId(x.getItemcode());
            return prodStorage;
        }).collect(Collectors.toList()));
    }


}




