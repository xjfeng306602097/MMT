package com.makro.mall.product.service;

import com.makro.mall.product.pojo.dto.ExcelDataDTO;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import com.makro.mall.product.pojo.vo.ProductVO;
import com.makro.mall.product.pojo.vo.SheetNameVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImportV4Service {
    SheetNameVo getSheetList(MultipartFile file);

    ExcelDataDTO getExcelDataFromSheetName(String uploadId, String sheetName);

    void importData(String mmCode, ProdTemplateInfo info, List<ProdList> prodListList);

    List<ProductVO> toProductData(List<ProdList> dataList);

    Boolean form(String mmCode, List<ProdList> dto);
}
