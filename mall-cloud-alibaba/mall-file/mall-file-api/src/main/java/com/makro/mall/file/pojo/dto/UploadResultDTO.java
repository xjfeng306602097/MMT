package com.makro.mall.file.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/16
 */
@Data
@Accessors(chain = true)
public class UploadResultDTO {

    private String path;

    private Double mb;

}
