package com.makro.mall.common.health;

import com.makro.mall.common.enums.HealthCheckStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/9
 */
@Data
public class HealthCheckResult {

    @ApiModelProperty("状态：2:无此组件, 1:正常, 0:不正常")
    private Integer status;
    @ApiModelProperty("信息")
    private String message;
    @ApiModelProperty("耗时(单位:毫秒)")
    private Long mills;
    @ApiModelProperty("名称")
    private String name;

    public HealthCheckResult fillCodeAndMsg(String name, HealthCheckStatusEnum healthCheckStatusEnum) {
        this.name = name;
        this.status = healthCheckStatusEnum.getStatus();
        this.message = healthCheckStatusEnum.getDesc();
        return this;
    }

}
