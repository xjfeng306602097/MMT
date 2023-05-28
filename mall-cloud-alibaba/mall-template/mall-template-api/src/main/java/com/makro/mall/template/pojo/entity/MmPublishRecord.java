package com.makro.mall.template.pojo.entity;

import com.makro.mall.mongo.model.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description MM模板发布相关
 * @date 2022/1/4
 */
@Data
@Document("mm_publish_record")
public class MmPublishRecord {

    @Indexed
    private String code;

    private List<MmPublishItem> items = new ArrayList<>();

    @Data
    public static class MmPublishItem extends BaseEntity {

        private String previewUrl;

        private String publishUrl;

        private String publisher;

    }

}
