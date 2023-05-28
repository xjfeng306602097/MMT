package com.makro.mall.message.component.mail;

import lombok.Data;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * @author xiaojunfeng
 * @description JavaMailSenderImpl包装类
 * @date 2021/11/6
 */
@Data
public class MakroMailSender {

    private JavaMailSenderImpl javaMailSender;

    private Integer limit;

    public static MakroMailSender init(JavaMailSenderImpl javaMailSender, Integer limit) {
        MakroMailSender makroMailSender = new MakroMailSender();
        makroMailSender.setJavaMailSender(javaMailSender);
        makroMailSender.setLimit(limit);
        return makroMailSender;
    }

}
