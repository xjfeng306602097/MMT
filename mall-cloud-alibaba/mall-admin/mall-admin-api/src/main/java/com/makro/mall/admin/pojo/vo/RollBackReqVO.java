package com.makro.mall.admin.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class RollBackReqVO {
    @ApiModelProperty("回滚到哪个状态 4/2")
    Long status;
}
