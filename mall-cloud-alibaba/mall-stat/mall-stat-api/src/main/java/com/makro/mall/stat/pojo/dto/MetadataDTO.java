package com.makro.mall.stat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataDTO {

    private String name;
    private String unit;
    private List<String> values;
    private List<String> values2;

    public MetadataDTO(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }
}
