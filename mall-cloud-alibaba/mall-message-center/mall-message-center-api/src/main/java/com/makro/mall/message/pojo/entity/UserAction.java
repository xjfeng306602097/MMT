package com.makro.mall.message.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document("user_action")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAction {

    @ApiModelProperty("请求url")
    private String url;

    @ApiModelProperty("请求方法")
    private String httpMethod;

    @ApiModelProperty("请求ip")
    private String ip;

    @ApiModelProperty("请求类信息")
    private String classMethod;

    @ApiModelProperty("请求参数")
    private String args;

    @ApiModelProperty("请求者Id")
    private String creator;

    @ApiModelProperty("请求者昵称")
    private String username;

    @ApiModelProperty("请求时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreate;
}
