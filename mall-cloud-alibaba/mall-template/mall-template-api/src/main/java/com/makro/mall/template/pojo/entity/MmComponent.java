package com.makro.mall.template.pojo.entity;

import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.mongo.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * @author xiaojunfeng
 * @description MM组件相关
 * @date 2021/10/27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document("mm_component")
@CompoundIndexes({
        @CompoundIndex(name = "idx_code_version", def = "{'code': 1, 'version': -1}")
})
public class MmComponent extends BaseEntity {

    @Indexed
    @NotNull
    @ApiModelProperty(value = "组件名称")
    private String name;

    @Indexed
    @NotNull
    @ApiModelProperty(value = "类型,1-Product,2-Page Head,3-Page Footer,4.Other", required = true)
    private Integer type;

    @ApiModelProperty(value = "组件code,新增不需要添加，更新需要加上", required = false)
    private String code;

    @ApiModelProperty(value = "组件内容,新增不需要加，更新加上", required = false)
    private JSONObject content;

    @ApiModelProperty(value = "预览图路径，调用文件服务上传后传入,更新时传入", required = false)
    private String previewUrl;

    @ApiModelProperty(value = "0=关闭 1=开启 2=删除")
    private Integer status;

    @ApiModelProperty(value = "版本号", required = false)
    private Integer version;

    @ApiModelProperty(value = "是否删除，0-未删除，1-删除", required = false)
    private Integer isDelete;

    @CreatedBy
    private String creator;

    @LastModifiedBy
    private String lastUpdater;

    public MmComponent incrVersion() {
        this.version++;
        return this;
    }

}
