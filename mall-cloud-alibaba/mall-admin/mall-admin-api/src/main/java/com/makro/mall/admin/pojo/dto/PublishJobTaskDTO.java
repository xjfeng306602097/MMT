package com.makro.mall.admin.pojo.dto;

import com.makro.mall.common.web.util.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/7/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PublishJobTaskDTO {

    private String taskId;

    private String createdBy;

    /**
     * 需要有对应session才触发调用该构造函数
     */
    public PublishJobTaskDTO(String taskId) {
        this.createdBy = JwtUtils.getUsername();
        this.taskId = taskId;
    }

}
