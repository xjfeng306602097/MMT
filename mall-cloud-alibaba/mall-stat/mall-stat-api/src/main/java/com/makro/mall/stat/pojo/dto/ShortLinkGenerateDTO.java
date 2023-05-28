package com.makro.mall.stat.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.LinkedMultiValueMap;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/8/1
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShortLinkGenerateDTO {

    private String originUrl;

    private LinkedMultiValueMap<String, String> params;

}
