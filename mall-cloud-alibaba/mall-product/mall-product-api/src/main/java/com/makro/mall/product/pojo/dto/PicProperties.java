package com.makro.mall.product.pojo.dto;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: zhuangzikai
 * @Date: 2021/11/29
 **/
@Data
public class PicProperties<T> {
    private T basicInfo;
    @JsonIgnore
    private String filePath;
    private boolean rgb = true;
    private String originPath;
    private String thumbnailPath;
    private String transformPath;
    private String transformThumbnailPath;
    private BigDecimal originWidth;
    private BigDecimal originHeight;
    private BigDecimal thumbnailWidth;
    private BigDecimal thumbnailHeight;
    private Integer temp;

    public static PicProperties convert(Object pic) {
        PicProperties target = new PicProperties();
        target.setBasicInfo(pic);
        BeanUtil.copyProperties(pic, target, CopyOptions.create().ignoreNullValue());
        // 将json解析成对应内容
        JSONObject json = JSON.parseObject(target.getFilePath());
        if (json != null) {
            boolean rgb = json.getBoolean("rgb");
            target.setRgb(json.getBoolean("rgb"));
            if (rgb) {
                target.setOriginPath(json.getString("originPath"));
                target.setThumbnailPath(json.getString("thumbnailPath"));
                target.setTransformPath(json.getString("transformPath"));
                target.setTransformThumbnailPath(json.getString("transformThumbnailPath"));
                target.setOriginWidth(json.getBigDecimal("originWidth"));
                target.setOriginHeight(json.getBigDecimal("originHeight"));
                target.setThumbnailWidth(json.getBigDecimal("thumbnailWidth"));
                target.setThumbnailHeight(json.getBigDecimal("thumbnailHeight"));
            } else {
                target.setOriginPath(json.getString("transformPath"));
                target.setThumbnailPath(json.getString("transformThumbnailPath"));
                target.setTransformPath(json.getString("originPath"));
                target.setTransformThumbnailPath(json.getString("thumbnailPath"));
                target.setOriginWidth(json.getBigDecimal("thumbnailWidth"));
                target.setOriginHeight(json.getBigDecimal("thumbnailHeight"));
                target.setThumbnailWidth(json.getBigDecimal("originWidth"));
                target.setThumbnailHeight(json.getBigDecimal("originHeight"));
            }
        }
        return target;
    }
}
