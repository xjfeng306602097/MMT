package com.makro.mall.message.pojo.dto;

import com.makro.mall.message.enums.MailTypeEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description H5邮件
 * @date 2021/11/3
 */
@Getter
@Setter
@NoArgsConstructor
public class H5MailMessageDTO extends MailMessageDTO {


    public H5MailMessageDTO(MailTypeEnum mailTypeEnum) {
        this.setMailTypeEnum(mailTypeEnum);
    }

    /**
     * 参数
     */
    private List<String> params;

    /**
     * 模板路径
     */
    private String path;

    /**
     * 模板内容，和path二选一
     */
    private String templateContent;

}
