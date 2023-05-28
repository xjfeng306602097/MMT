package com.makro.mall.message.dto;

import com.makro.mall.message.pojo.entity.SmsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SmsMessagePageFeignDTO {
    private long getTotalElements;
    private List<SmsMessage> content;
}
