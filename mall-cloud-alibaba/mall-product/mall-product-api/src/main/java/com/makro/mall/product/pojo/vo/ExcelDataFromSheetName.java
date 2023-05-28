package com.makro.mall.product.pojo.vo;

import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExcelDataFromSheetName {

    ProdTemplateInfo info;
    List<ExcelDataVO> templateDataVOList;
}
