package com.makro.mall.admin.pojo.vo;

import lombok.Data;

import java.util.Collection;
import java.util.Set;

@Data
public class VerifyCustomerRepVO {
    private Set<Long> customerIds;
    private Collection<String> errors;
}
