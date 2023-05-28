package com.makro.mall.message.pojo.dto;

import com.makro.mall.message.enums.MailTypeEnum;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/10
 */
public class TextMailMessageDTO extends MailMessageDTO {

    public TextMailMessageDTO() {
        this.setMailTypeEnum(MailTypeEnum.TEXT);
    }

}
