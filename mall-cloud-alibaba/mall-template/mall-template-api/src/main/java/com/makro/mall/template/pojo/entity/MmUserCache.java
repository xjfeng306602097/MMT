package com.makro.mall.template.pojo.entity;

import com.alibaba.fastjson2.JSONObject;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author xiaojunfeng
 * @description MM组件相关
 * @date 2021/10/27
 */
@Data
@Document("mm_user_cache")
public class MmUserCache {
    @Id
    @ApiModelProperty(hidden = true)
    private String username;
    @ApiModelProperty("'json对象")
    private JSONObject jsonObject;


}
