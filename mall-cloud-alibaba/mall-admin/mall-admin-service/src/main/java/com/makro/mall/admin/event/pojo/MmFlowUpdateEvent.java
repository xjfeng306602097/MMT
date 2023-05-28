package com.makro.mall.admin.event.pojo;

import lombok.Data;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/4/6
 */
@Data
public class MmFlowUpdateEvent {

    public MmFlowUpdateEvent(Long id, String code, String flowType, boolean isAutoApprove) {
        this.id = id;
        this.code = code;
        this.flowType = flowType;
        this.isAutoApprove = isAutoApprove;
    }

    public MmFlowUpdateEvent(Long id, String code, String flowType, Integer step, String configJson, boolean isCreate,
                             String currentRole) {
        this.id = id;
        this.code = code;
        this.flowType = flowType;
        this.step = step;
        this.configJson = configJson;
        this.isCreate = isCreate;
        this.currentRole = currentRole;
    }

    private Long id;

    private String code;

    private String flowType;

    private Integer step;

    private String configJson;

    private boolean isCreate;

    private String currentRole;

    private boolean isAutoApprove = false;

}
