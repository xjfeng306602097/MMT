package com.makro.mall.message.pojo.dto;

import lombok.Data;

/**
 * @author xiaojunfeng
 * @description 邮件发送结果
 * @date 2021/11/4
 */
@Data
public class SendMailResultDTO<T> {

    private String id;

    private String[] toUser;

    private boolean isSuccess;

    private String msg;

    private Integer successCount;

    private Integer failCount;

    private T mail;

    public static SendMailResultDTO init(String id, String[] toUser) {
        SendMailResultDTO dto = new SendMailResultDTO();
        dto.setToUser(toUser);
        dto.setId(id);
        return dto;
    }

    public SendMailResultDTO success(Integer successCount) {
        this.successCount = successCount;
        this.isSuccess = true;
        return this;
    }

    public SendMailResultDTO fail(String msg) {
        this.msg = msg;
        this.isSuccess = false;
        this.successCount = 0;
        return this;
    }

}
