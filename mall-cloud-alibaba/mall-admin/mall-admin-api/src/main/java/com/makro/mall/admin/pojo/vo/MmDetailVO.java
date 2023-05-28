package com.makro.mall.admin.pojo.vo;

import com.makro.mall.admin.pojo.entity.MmDetail;
import lombok.Data;

/**
 * @author xiaojunfeng
 * @description mmdetail返回对象
 * @date 2021/11/22
 */
@Data
public class MmDetailVO<D> extends MmDetail {

    /**
     * ProdData
     */
    private D prodData;

}
