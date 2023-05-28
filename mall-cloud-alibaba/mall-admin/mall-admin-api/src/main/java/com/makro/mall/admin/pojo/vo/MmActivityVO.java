package com.makro.mall.admin.pojo.vo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.admin.pojo.entity.MmSegment;
import com.makro.mall.admin.pojo.entity.MmStore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 功能描述:
 * 在MM里头存储的storeType,memberType是对应的code，
 * 返回前端的接口考虑增加参数storeTypeName, memberTypeName两个参数，
 * 分别从MmStore、MmMemberType表中获取,并将其写到activity中
 *
 * @Author: 卢嘉俊
 * @Date: 2022/5/24
 */
@Data
public class MmActivityVO {
    @TableField(fill = FieldFill.INSERT)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime gmtCreate;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    protected LocalDateTime gmtModified;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     *
     */
    @ApiModelProperty(value = "活动code，新增自动生成，更新时传入", required = false)
    private String mmCode;
    /**
     *
     */
    @ApiModelProperty(value = "店铺code,新增必传", required = true)
    private String storeCode;
    /**
     *
     */
    @ApiModelProperty(value = "店铺对象", required = true)
    private List<MmStore> store;
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
     *
     */
    @ApiModelProperty(value = "memberType对象", required = true)
    private List<MmMemberType> memberTypes;
    /**
     * 标题
     */
    @ApiModelProperty(value = "名称，新增必传", required = true)
    private String title;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注", required = false)
    @Length(max = 200)
    private String remark;
    /**
     *
     */
    @ApiModelProperty(value = "活动起始时间", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     *
     */
    @ApiModelProperty(value = "活动结束时间", required = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 模板code
     */
    @ApiModelProperty(value = "模板code，新增不传，更新传", required = false)
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

    private String segment;
    @ApiModelProperty(value = "segment 列表")
    private List<MmSegment> segments;
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
    @Length(max = 200)
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


    @ApiModelProperty(value = "是否快速创建模板,0-否，1-是，默认0")
    private Boolean isQuickCreate;

    @ApiModelProperty(value = "关联商品SEGMENT id")
    private String productSegment;

    @ApiModelProperty(value = "app名称")
    private String appTitle;
}
