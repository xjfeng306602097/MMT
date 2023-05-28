package com.makro.mall.template.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author xiaojunfeng
 * @description MM模板相关
 * @date 2021/10/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document("mm_template_draft")
@CompoundIndexes({
        @CompoundIndex(name = "idx_code_version", def = "{'code': 1, 'version': -1}")
})
public class MmTemplateDraft extends MmTemplate {

    public MmTemplateDraft clear() {
        this.id = null;
        return this;
    }

}
