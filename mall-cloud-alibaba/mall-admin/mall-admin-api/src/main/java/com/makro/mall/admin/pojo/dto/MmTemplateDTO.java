package com.makro.mall.admin.pojo.dto;

import com.alibaba.fastjson2.JSONArray;
import com.makro.mall.admin.pojo.entity.SysUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class MmTemplateDTO {

    @NotNull
    private String name;

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

    @ApiModelProperty(required = true)
    @NotNull
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


    private Integer zoomPosition;

    @ApiModelProperty(value = "纸张方向,0-竖排，1-横排")
    private Integer paperOrientation;

    @ApiModelProperty(value = "商品数量,不需要传")
    private Integer goodsCount;

    @ApiModelProperty(value = "页面数量,不需要传")
    private Integer pageCount;

    @ApiModelProperty(value = "页面数量")
    private Integer templatePageTotal;

    private String previewMap;

    private String bgColor;

    @ApiModelProperty(value = "创建类型,0-标准通用模板，1-非标准模板", required = true)
    private String createType;

    @ApiModelProperty(value = "是否删除,1-已删除，0-未删除", required = true)
    private Integer isDelete;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "presetId")
    private Integer presetId;

    @ApiModelProperty(value = "release")
    private Boolean release;

    @ApiModelProperty(value = "扩展字段,前端使用")
    private String pageOption;

    @ApiModelProperty(value = "扩展字段,前端使用")
    private JSONArray pageConfigs;

    private String fonts;

    @ApiModelProperty(value = "configW / configH")
    private BigDecimal rate;

    @ApiModelProperty(value = "设计器是否有人使用")
    private Boolean lock;

    @ApiModelProperty(value = "设计器使用者信息")
    private SysUser lockUser;

}
