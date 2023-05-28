package com.makro.mall.message.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class GGGSmsMessageDTO extends SmsMessageDTO {

    private String jobId;
    private Long sendTime;

}
