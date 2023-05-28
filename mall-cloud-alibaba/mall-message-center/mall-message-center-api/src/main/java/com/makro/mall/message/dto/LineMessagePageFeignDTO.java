package com.makro.mall.message.dto;

import com.makro.mall.message.pojo.entity.LineSendMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LineMessagePageFeignDTO {
    private long getTotalElements;
    private List<LineSendMessage> content;
}
