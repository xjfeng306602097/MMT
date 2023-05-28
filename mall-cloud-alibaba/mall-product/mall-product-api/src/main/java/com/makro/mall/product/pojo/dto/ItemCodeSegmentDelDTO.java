package com.makro.mall.product.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemCodeSegmentDelDTO {
    /**
     *
     */
    private List<String> itemCode;
    /**
     *
     */
    private List<Long> segmentId;

}
