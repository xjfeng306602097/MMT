package com.makro.mall.product.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SheetNameVo {
    List<String> sheets;
    String uploadId;


}
