package com.makro.mall.message.pojo.dto;

import com.makro.mall.common.model.BasePageRequest;
import lombok.Data;

import java.util.Date;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/1/16
 */
@Data
public class UnsubscribeLogPageReq extends BasePageRequest {

    private String address;

    private Date begin;

    private Date end;

}
