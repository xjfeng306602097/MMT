package com.makro.mall.product.pojo.dto;

import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.entity.ProdPrice;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExcelDataDTO {

    private ProdTemplateInfo info;
    private List<ProdListV4DTO> dataList;
    private List<IndexV4DTO> index;

}
