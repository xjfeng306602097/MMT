package com.makro.mall.admin.pojo.dto;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class MmActivityPageRepDTO extends BaseEntity {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     *
     */
    @ApiModelProperty(value = "活动code，新增自动生成，更新时传入")
    private String mmCode;
    /**
     *
     */
    @ApiModelProperty(value = "店铺code,新增必传", required = true)
    private String storeCode;
    /**
     * 类型
     */
    @ApiModelProperty(value = "类型，新增必传", required = true)
    private String type;
    /**
     *
     */
    @ApiModelProperty(value = "memberType,默认传0", required = true)
    private String memberType;
    /**
     * 标题
     */
    @ApiModelProperty(value = "名称，新增必传", required = true)
    private String title;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @Length(max = 200)
    private String remark;
    /**
     *
     */
    @ApiModelProperty(value = "活动起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     *
     */
    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 模板code
     */
    @ApiModelProperty(value = "模板code，新增不传，更新传")
    private String mmTemplateCode;
    @TableField(exist = false)
    private Integer itemCount;
    /**
     * 1-正常，2-下架,
     */
    private Long status;
    /**
     * 0-初始化，1-生成成功,2-生成失败
     */
    private Long publishStatus;

    @ApiModelProperty(value = "app发布的路径")
    private String appUrl;

    @ApiModelProperty(value = "H5发布的路径")
    private String publishUrl;
    /**
     * 预览图链接
     */
    private String previewUrl;
    /**
     * 最新一次发布的路径
     */
    private String pages;
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;
    /**
     * 0-未发起，1-已发起
     */
    private Long approvalInitiated;
    /**
     * segment id 使用逗号分割
     */
    private String segment;
    /**
     * 预留字段1
     */
    @ApiModelProperty(value = "预留字段1")
    @Length(max = 200)
    private String reserve1;
    /**
     * 预留字段2
     */
    @ApiModelProperty(value = "预留字段2")
    private String reserve2;
    /**
     * 预留字段3
     */
    @ApiModelProperty(value = "预留字段3")
    @Length(max = 200)
    private String reserve3;
    /**
     * 预留字段4
     */
    @ApiModelProperty(value = "预留字段4")
    @Length(max = 200)
    private String reserve4;
    /**
     * 预留字段5
     */
    @ApiModelProperty(value = "预留字段5")
    @Length(max = 200)
    private String reserve5;

    @ApiModelProperty(value = "模板消息")
    private MmTemplateDTO templateInfo;

    @ApiModelProperty(value = "关联商品SEGMENT id")
    private String productSegment;


    @ApiModelProperty(value = "app名称")
    private String appTitle;
}
