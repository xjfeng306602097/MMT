package com.makro.mall.file.pojo.vo;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.file.pojo.entity.MmElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class MmElementVO extends MmElement {

    private boolean rgb = true;

    private String originPath;

    private String thumbnailPath;

    private String transformPath;

    private String transformThumbnailPath;

    private BigDecimal originWidth;

    private BigDecimal originHeight;

    private BigDecimal thumbnailWidth;

    private BigDecimal thumbnailHeight;

    public static MmElementVO convert(MmElement element) {
        MmElementVO target = new MmElementVO();
        BeanUtil.copyProperties(element, target, CopyOptions.create().ignoreNullValue());
        // 将json解析成对应内容
        JSONObject json = JSON.parseObject(target.getFilePath());
        if (json != null) {
            target.setRgb(json.getBoolean("rgb"));
            target.setOriginPath(json.getString("originPath"));
            target.setThumbnailPath(json.getString("thumbnailPath"));
            target.setTransformPath(json.getString("transformPath"));
            target.setTransformThumbnailPath(json.getString("transformThumbnailPath"));
            target.setOriginWidth(json.getBigDecimal("originWidth"));
            target.setOriginHeight(json.getBigDecimal("originHeight"));
            target.setThumbnailWidth(json.getBigDecimal("thumbnailWidth"));
            target.setThumbnailHeight(json.getBigDecimal("thumbnailHeight"));
        }
        return target;
    }

}
