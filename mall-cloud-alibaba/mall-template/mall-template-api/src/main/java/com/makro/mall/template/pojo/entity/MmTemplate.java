package com.makro.mall.template.pojo.entity;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.mongo.model.BaseEntity;
import io.swagger.annotations.ApiModel;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description MM模板相关
 * @date 2021/10/31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Document("mm_template")
@CompoundIndexes({
        @CompoundIndex(name = "idx_code_version", def = "{'code': 1, 'version': -1}")
})
public class MmTemplate extends BaseEntity {

    public static final Integer FORBIDDEN_OPERATE_STATUS = 2;
    private static final Integer IS_VALID = 1;
    @Indexed
    @NotNull
    private String name;
    @Indexed
    @ApiModelProperty(value = "mmCode", notes = "没有mmCode为通用模板，有mmCode则是和mm关联的模板")
    private String mmCode;

    @ApiModelProperty(value = "来源模板Code", notes = "标记由哪个模板更新而来")
    private String originCode;

    @ApiModelProperty(value = "来源模板Version", notes = "标记由哪个模板版本更新而来")
    private Integer originVersion;

    @ApiModelProperty(value = "编码", notes = "新增的时候不需要传，更新要")
    private String code;

    @ApiModelProperty(value = "预览图文件路径,可调用文件服务上传后填入")
    private String previewPath;

    @ApiModelProperty(value = "背景图文件路径,可调用文件服务上传后填入")
    private String bgPath;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal pageWidth;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal pageHeight;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal configW;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal configH;

    @ApiModelProperty(required = true)
    @NotNull
    private Long configDpi;

    @ApiModelProperty(required = true)
    @NotNull
    private Long configUnitID;

    private String configUnitName;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal bleedLineTop;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal bleedLineBottom;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal bleedLineIn;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal bleedLineOut;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal marginTop;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal marginBottom;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal marginIn;

    @ApiModelProperty(required = true)
    @NotNull
    private BigDecimal marginOut;

    @ApiModelProperty(required = false)
    private Integer zoomPosition;

    @ApiModelProperty(value = "纸张方向,0-竖排，1-横排")
    private Integer paperOrientation;

    @ApiModelProperty(value = "商品数量,不需要传", required = false)
    private Integer goodsCount;

    @ApiModelProperty(value = "页面数量,不需要传", required = false)
    private Integer pageCount;

    private String previewMap;

    private String bgColor;

    @ApiModelProperty(value = "创建类型,0-标准通用模板，1-非标准模板", required = true)
    private String createType;

    @CreatedBy
    private String creator;
    @LastModifiedBy
    private String lastUpdater;

    @ApiModelProperty(value = "状态,0=新建（待设计）1=设计中（开始设计）2=设计完成，审核中 3=审核完成 (可供快捷选择) 10=作废, 创建时默认为0", required = true)
    private Integer status;

    @ApiModelProperty(value = "是否删除,1-已删除，0-未删除", required = true)
    private Integer isDelete;

    @ApiModelProperty(value = "版本号", required = false)
    private Integer version;

    @ApiModelProperty(value = "presetId", required = false)
    private Integer presetId;

    @ApiModelProperty(value = "release", required = false)
    private Boolean release;

    @ApiModelProperty(value = "扩展字段,前端使用", required = false)
    private String pageOption;

    @ApiModelProperty(value = "扩展字段,前端使用", required = false)
    private JSONArray pageConfigs;

    @ApiModelProperty(value = "type,用于区分是标准模板还是MM模板,1-标准,2-MM模板", required = false)
    private Integer type;

    private String fonts;

    @ApiModelProperty(value = "页面数量")
    private Integer templatePageTotal;

    private List<MMTemplatePage> templatePageList = new ArrayList<>();

    private Long approvalInitiated = 0L;

    public MmTemplate incrVersion() {
        this.version++;
        return this;
    }

    public MmTemplate filterInvalidPageAndSort() {
        // 过滤删除图片并进行排序
        this.templatePageList = this.templatePageList.stream()
                .filter(p -> IS_VALID.equals(p.getIsValid())).sorted(Comparator.comparing(MMTemplatePage::getSort))
                .collect(Collectors.toList());
        return this;
    }

    public MmTemplate filterAndResetPage() {
        this.templatePageList = this.templatePageList.stream()
                .filter(p -> IS_VALID.equals(p.getIsValid())).map(p -> {
                    p.setTemplateCode(this.getCode());
                    p.init();
                    return p;
                }).collect(Collectors.toList());
        return this;
    }

    @Document
    @Data
    @EqualsAndHashCode(callSuper = true)
    @ToString(callSuper = true)
    @ApiModel("模板页")
    public static class MMTemplatePage extends BaseEntity {

        @ApiModelProperty(value = "页面编码,页面更新时再传", required = false)
        private String code;

        @ApiModelProperty(value = "模板编码", required = false)
        private String templateCode;

        @ApiModelProperty(value = "页面json内容", required = true)
        private JSONObject content;

        @ApiModelProperty(value = "序号", required = true)
        private Integer sort;

        @ApiModelProperty(value = "是否有效,1-正常，0-下线", required = true)
        private Integer isValid;

        @ApiModelProperty(value = "保存方式, 0-自动保存,1-强制保存", required = true)
        private Integer storageType;

        @ApiModelProperty(value = "版本号", required = false)
        private Integer version;

        @CreatedBy
        private String creator;

        @LastModifiedBy
        private String lastUpdater;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            MMTemplatePage that = (MMTemplatePage) o;
            return code.equals(that.code) && content.equals(that.content) && sort.equals(that.sort) && isValid.equals(that.isValid) && storageType.equals(that.storageType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(code, content, sort, isValid, storageType);
        }

        public MMTemplatePage incrVersion() {
            this.version++;
            return this;
        }

        public MMTemplatePage init() {
            this.code = IdUtil.randomUUID();
            this.version = 0;
            return this;
        }

    }

}
