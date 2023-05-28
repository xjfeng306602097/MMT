package com.makro.mall.stat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SummaryOfClicks2DTO {
   private List<String> list;
   private String value;
}
