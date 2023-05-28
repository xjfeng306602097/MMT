package com.makro.mall.admin.event.listener;

import com.makro.mall.admin.component.MmFlowContext;
import com.makro.mall.admin.event.pojo.MmFlowConfigUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

/**
 * @author xiaojunfeng
 * @description 流程配置修改事务监听
 * @date 2022/4/6
 */
@Component
@Slf4j
public class MmFlowConfigUpdateListener {

    @Resource
    private MmFlowContext mmFlowContext;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processConfigUpdate(MmFlowConfigUpdateEvent event) {
        log.info("流程配置id-{}被修改，触发流程容器刷新", event.getId());
        mmFlowContext.resetConfigMap();
    }

}
