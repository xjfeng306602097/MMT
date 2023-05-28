package com.makro.mall.message.vo;

import com.makro.mall.common.enums.MessageSendEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 功能描述:
 *
 * @Author: 卢嘉俊
 * @Date: 2022/8/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailMessagePageReqVO {
    @ApiModelProperty("状态,用,隔开")
    private MessageSendEnum status;
    @ApiModelProperty("页码")
    private Integer page;
    @ApiModelProperty("每页数量")
    private Integer size;
    @ApiModelProperty("是否删除")
    private Integer isDelete;
    @ApiModelProperty("发布id")
    private String mmPublishJobEmailTaskId;
    @ApiModelProperty("发送用户")
    private String[] toUser;
}
