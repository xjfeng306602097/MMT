package com.makro.mall.admin.pojo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author Ljj
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MmActivityBatchReqVO {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    @ApiModelProperty(value = "textthai数据")
    List<ExcelDataVO> templateDataVOList;

    @ApiModelProperty(value = "店铺code,新增必传", required = true)
    @NotNull
    private String storeCode;

    @ApiModelProperty(value = "类型，新增必传", required = true)
    @NotNull
    private String type;

    @ApiModelProperty(value = "memberType,默认传0", required = true)
    @NotNull
    private String memberType;

    @ApiModelProperty(value = "名称，新增必传", required = true)
    @Length(max = 200)
    private String title;

    @ApiModelProperty(value = "备注")
    @Length(max = 200)
    private String remark;

    @ApiModelProperty(value = "活动起始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date startTime;

    @ApiModelProperty(value = "活动结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull
    private Date endTime;

    @ApiModelProperty(value = "segment id 使用逗号分割")
    @NotNull
    private String segment;

    @ApiModelProperty(value = "bounceRate,默认30s")
    private Long bounceRate;

}
