package com.makro.mall.stat.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.makro.mall.common.model.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/10/10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PageStaySummaryRequest extends BasePageRequest {

    @NotNull
    private String mmCode;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    private boolean desc = true;

    private List<String> customerTypes = new ArrayList<String>();

    private List<String> channels = new ArrayList<String>();

}
