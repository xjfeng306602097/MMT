package com.makro.mall.file.pojo.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author xiaojunfeng
 * @description 上传返回结果接口
 * @date 2021/11/24
 */
@Data
public class PicUploadDTO {

    private boolean isRgb = true;

    private String originPath;

    private String thumbnailPath;

    private String transformPath;

    private String transformThumbnailPath;

    private BigDecimal originWidth;

    private BigDecimal originHeight;

    private BigDecimal thumbnailWidth;

    private BigDecimal thumbnailHeight;

}
