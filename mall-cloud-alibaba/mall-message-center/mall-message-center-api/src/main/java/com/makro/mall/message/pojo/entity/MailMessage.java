package com.makro.mall.message.pojo.entity;

import com.makro.mall.common.enums.MessageSendEnum;
import com.makro.mall.message.enums.MailTypeEnum;
import com.makro.mall.mongo.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description 邮件数据
 * @date 2021/11/3
 */
@Data
@Document("mail_message")
@CompoundIndexes({
        @CompoundIndex(name = "idx_code_version", def = "{'code': 1, 'version': -1}")
})
public class MailMessage extends BaseEntity {

    public static final Long MAIL_SEND_DELAY_THRESHOLD = 86400L;

    /**
     * 发送者
     */
    private String sender = "default";

    /**
     * 收件人邮箱
     */
    @Indexed
    private String[] toUser;

    @ApiModelProperty("客户Id")
    private Long customerId;

    /**
     * 抄送人邮箱
     */
    private String toCc;
    /**
     * 邮件主题
     */
    private String subject;

    /**
     * 内容
     */
    private String content;

    /**
     * 邮件发送类型
     */
    private MailTypeEnum mailTypeEnum;


    @ApiModelProperty(value = "状态,0-待发送，1-发送成功,2-部分发送成功,3-失败", required = true)
    private MessageSendEnum status;

    @CreatedBy
    private String creator;

    @LastModifiedBy
    private String lastUpdater;

    @ApiModelProperty(value = "H5相关内容", required = false)
    private H5MailInfo h5MailInfo;

    private Integer successCount = 0;

    @ApiModelProperty(value = "是否删除,1-已删除，0-未删除", required = true)
    private Integer isDelete;

    @ApiModelProperty(value = "延期执行时间,单位为秒,默认为0表示立即执行发送", required = false)
    private Long delay = 0L;

    @Indexed
    @ApiModelProperty(value = "mm发布任务id")
    private String mmPublishJobEmailTaskId;

    @Data
    @Document
    public static class H5MailInfo {
        /**
         * 参数
         */
        private List<String> params;

        /**
         * 模板路径
         */
        private String path;

        /**
         * 和path 二选一
         */
        private String templateContent;

    }


}
