package com.makro.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.product.pojo.dto.ItemCodeSegmentDelDTO;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.vo.ProdStoragePageVO;
import com.makro.mall.product.pojo.vo.ProdStorageVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface ProdStorageService extends IService<ProdStorage> {
    Boolean saveOrUpdateProdStorage(ProdStorage storage);

    Boolean batchSaveOrUpdateProdStorage(Collection<ProdStorage> list);

    IPage<ProdStoragePageVO> list(Page<ProdStorage> page, ProdStorage storage, String segmentId);

    List<ProdStorageVO> parseExcel(MultipartFile file, String segmentName);

    void storageValidated(List<ProdStorageVO> prodStorageVO);

    void createOrUpdateBatch(List<ProdStorageVO> prodStorageVOS);

    Boolean delItemCodeSegment(ItemCodeSegmentDelDTO dto);

    Boolean delete(String id);
}
