package com.makro.mall.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/1/19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailSubscriptionFeignDTO {

    private List<String> addresses;

    private Integer status;
}
