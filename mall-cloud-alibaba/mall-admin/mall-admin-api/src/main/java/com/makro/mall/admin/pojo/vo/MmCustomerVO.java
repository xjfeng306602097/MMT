package com.makro.mall.admin.pojo.vo;

import com.makro.mall.admin.pojo.entity.MmMemberType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * 功能描述: 客户VO
 * 客户资料上传&Segment维护
 *
 * @Author: 卢嘉俊
 * @Date: 2022/5/12
 */
@Data
public class MmCustomerVO {


    @ApiModelProperty("segment列表")
    Set<MmSegmentVO> segments;

    @ApiModelProperty("memberType id列表")
    Set<MmMemberType> memberTypes;
    private Long id;
    @NotBlank(message = "客户名称不为空")
    @ApiModelProperty("客户名称")
    private String name;

    @ApiModelProperty("客户手机号")
    @NotBlank(message = "客户手机号不为空")
    private String phone;

    @ApiModelProperty("客户邮箱")
    private String email;

    @ApiModelProperty("客户编码")
    private String customerCode;

    @ApiModelProperty("和mm绑定，之前每做一期就导入一批")
    private String mmCode;

    @ApiModelProperty("line 的 id")
    private String lineId;

    @ApiModelProperty("导入判断是否存在")
    private Boolean isExist;

    @ApiModelProperty("line主体,保存lineId时需指定主体")
    private String lineBotChannelToken;

}
