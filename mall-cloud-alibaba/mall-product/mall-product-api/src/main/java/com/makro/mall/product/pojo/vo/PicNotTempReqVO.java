package com.makro.mall.product.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Data
@EqualsAndHashCode
@ToString
public class PicNotTempReqVO {
    private List<Long> ids;
}
