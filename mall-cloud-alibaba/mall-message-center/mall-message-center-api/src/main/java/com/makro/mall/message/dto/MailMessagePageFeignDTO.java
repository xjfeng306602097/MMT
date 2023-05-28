package com.makro.mall.message.dto;

import com.makro.mall.message.pojo.entity.MailMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailMessagePageFeignDTO {
    private long getTotalElements;
    private List<MailMessage> content;
}
