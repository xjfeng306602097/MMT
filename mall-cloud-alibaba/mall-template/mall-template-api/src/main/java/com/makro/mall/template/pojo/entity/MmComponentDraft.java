package com.makro.mall.template.pojo.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author xiaojunfeng
 * @description MM组件版本管理
 * @date 2021/10/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document("mm_component_draft")
@CompoundIndexes({
        @CompoundIndex(name = "idx_code_version", def = "{'code': 1, 'version': -1}")
})
public class MmComponentDraft extends MmComponent {

    public MmComponentDraft clear() {
        this.id = null;
        return this;
    }

}
