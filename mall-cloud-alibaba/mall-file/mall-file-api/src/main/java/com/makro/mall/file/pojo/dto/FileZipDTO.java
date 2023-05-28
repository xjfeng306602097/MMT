package com.makro.mall.file.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/4/12
 */
@Data
public class FileZipDTO {

    @ApiModelProperty(value = "压缩文件名称", required = false)
    private String zipName;

    @ApiModelProperty(value = "文件列表", required = false)
    private List<FileEntry> files;

    @Data
    @ApiModel(value = "fileEntry")
    public static class FileEntry {

        @ApiModelProperty(value = "压缩文件中的文件路径", required = false)
        private String filePath;

        @ApiModelProperty(value = "对应的在文件服务器的路径", required = false)
        private String file;

    }

}
