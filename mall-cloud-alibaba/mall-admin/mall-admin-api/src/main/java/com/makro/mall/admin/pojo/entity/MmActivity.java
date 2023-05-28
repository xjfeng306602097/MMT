package com.makro.mall.admin.pojo.entity;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.base.Joiner;
import com.makro.mall.common.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * MM活动表
 *
 * @TableName MM_ACTIVITY
 */
@EqualsAndHashCode(callSuper = true)
@TableName(value = "MM_ACTIVITY")
@Data
public class MmActivity extends BaseEntity implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "活动code，新增自动生成，更新时传入")
    private String mmCode;

    @ApiModelProperty(value = "店铺code,新增必传", required = true)
    private String storeCode;

    @ApiModelProperty(value = "类型，新增必传", required = true)
    private String type;

    @ApiModelProperty(value = "memberType,默认传0", required = true)
    private String memberType;

    @ApiModelProperty(value = "名称，新增必传", required = true)
    private String title;

    @ApiModelProperty(value = "备注")
    @Length(max = 200)
    private String remark;

    @ApiModelProperty(value = "活动起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty(value = "模板code，新增不传，更新传")
    private String mmTemplateCode;
    @TableField(exist = false)
    private Integer itemCount;
    /**
     * 0新建 New
     * 1待设计 To Be Designed
     * 2设计中 Designing
     * 3设计审批 Design Submitted For Review
     * 4设计审批完 Design Completed
     * 5发布审批 Publish Submitted For Review
     * 6审批完成 Publish Completed
     * 10作废 Abolished
     */
    @ApiModelProperty(value = "" +
            "0新建 New\n" +
            "1待设计 To Be Designed\n" +
            "2设计中 Designing\n" +
            "3设计审批 Design Submitted For Review\n" +
            "4设计审批完 Design Completed\n" +
            "5发布审批 Publish Submitted For Review\n" +
            "6审批完成 Publish Completed\n" +
            "10作废 Abolished")
    private Long status;

    @ApiModelProperty(value = "0-初始化，1-生成成功,2-生成失败")
    private Long publishStatus;

    @ApiModelProperty(value = "app发布的路径")
    private String appUrl;

    @ApiModelProperty(value = "H5发布的路径")
    private String publishUrl;

    @ApiModelProperty(value = "预览图链接")
    private String previewUrl;

    @ApiModelProperty(value = "最新一次发布的路径")
    private String pages;
    @TableLogic(value = "0", delval = "1")
    private Integer deleted;
    @TableField(fill = FieldFill.INSERT)
    private String creator;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String lastUpdater;

    @ApiModelProperty(value = "0-未发起，1-已发起")
    private Long approvalInitiated;

    @ApiModelProperty(value = "segment id 使用逗号分割")
    private String segment;

    @ApiModelProperty(value = "预留字段1")
    @Length(max = 200)
    private String reserve1;

    @ApiModelProperty(value = "预留字段2")
    private String reserve2;

    @ApiModelProperty(value = "预留字段3")
    @Length(max = 200)
    private String reserve3;

    @ApiModelProperty(value = "预留字段4")
    @Length(max = 200)
    private String reserve4;

    @ApiModelProperty(value = "预留字段5")
    @Length(max = 200)
    private String reserve5;

    @TableField(exist = false)
    @ApiModelProperty(value = "bounceRate,默认30s")
    private Long bounceRate;

    @ApiModelProperty(value = "是否快速创建模板,0-否，1-是，默认0")
    private Boolean isQuickCreate = false;

    @ApiModelProperty(value = "关联商品SEGMENT id")
    private String productSegment;

    @ApiModelProperty(value = "app名称")
    private String appTitle;

    public MmActivity filterDuplicate() {
        if (StrUtil.isNotEmpty(this.storeCode)) {
            setStoreCode(Joiner.on(",").join(Stream.of(this.storeCode.split(",")).distinct().collect(Collectors.toList())));
        }
        return this;
    }
}
