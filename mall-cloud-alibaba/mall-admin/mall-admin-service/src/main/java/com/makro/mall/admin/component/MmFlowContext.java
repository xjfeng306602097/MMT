package com.makro.mall.admin.component;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.makro.mall.admin.pojo.entity.MmFlowConfig;
import com.makro.mall.admin.pojo.entity.MmFlowConfigItem;
import com.makro.mall.admin.service.MmFlowConfigService;
import com.makro.mall.common.web.util.JwtUtils;
import lombok.Data;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/1/29
 */
@Component
@Data
public class MmFlowContext implements InitializingBean {

    private Map<String, List<MmFlowConfigItem>> configMap;

    private Map<String, Long> statusMap;

    private List<Map<String, String>> configList = new ArrayList<>();

    private static final Object RESET_CONFIG_MONITOR = new Object();

    @Resource
    private MmFlowConfigService mmFlowConfigService;

    private final ThreadLocal<List<MmFlowConfigItem>> CONFIG_ITEM_THREAD_LOCAL = new ThreadLocal<>();

    public void resetConfigMap() {
        Map<MmFlowConfig, List<MmFlowConfigItem>> map = mmFlowConfigService.getConfigItems();
        synchronized (RESET_CONFIG_MONITOR) {
            configMap = map.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getCode(), Map.Entry<MmFlowConfig, List<MmFlowConfigItem>>::getValue));
            List<Map<String, String>> tempList = new ArrayList<>();
            Map<String, Long> tempMap = new HashMap<>();
            for (MmFlowConfig item : map.keySet()) {
                Map<String, String> mapping = new HashMap<>();
                mapping.put("name", item.getName());
                mapping.put("code", item.getCode());
                mapping.put("relatePermission", item.getRelatePermission());
                mapping.put("id", item.getId().toString());
                tempList.add(mapping);
                tempMap.put(item.getCode(), item.getStatus());
            }
            configList = tempList;
            statusMap = tempMap;
        }
    }

    /**
     * 判断流程是否上线
     *
     * @param flowType
     * @return
     */
    public boolean isWorkflowOn(String flowType) {
        return statusMap.get(flowType).equals(1L);
    }

    /**
     * 注册配置
     *
     * @param configJson
     * @param flowName
     */
    public List<MmFlowConfigItem> registerConfig(String configJson, String flowName) {
        if (StrUtil.isNotEmpty(configJson)) {
            CONFIG_ITEM_THREAD_LOCAL.set(JSON.parseArray(configJson, MmFlowConfigItem.class));
        } else {
            CONFIG_ITEM_THREAD_LOCAL.set(configMap.get(flowName));
        }
        return CONFIG_ITEM_THREAD_LOCAL.get();
    }

    /**
     * 使用完流程相关api接口后触发
     */
    public void removeConfig() {
        CONFIG_ITEM_THREAD_LOCAL.remove();
    }

    /**
     * 获取流程名称
     *
     * @return
     */
    public List<Map<String, String>> getFlows() {
        return configList;
    }

    /**
     * 获取流程对应的所有角色
     *
     * @param flowName
     * @return
     */
    public List<String> getRolesByFlow(String flowName) {
        return configMap.get(flowName).stream().map(MmFlowConfigItem::getRoleCode).collect(Collectors.toList());
    }

    /**
     * 获取第一步
     *
     * @param flowName
     * @return
     */
    public MmFlowConfigItem getByStep(String flowName, Integer step) {
        return configMap.get(flowName).get(step);
    }


    public boolean containsFlow(String flowName) {
        return configMap.containsKey(flowName);
    }

    /**
     * 根据名称
     *
     * @param flowName
     * @return
     */
    public List<MmFlowConfigItem> getConfigsByFlowName(String flowName) {
        return configMap.get(flowName);
    }

    /**
     * 判断当前用户是否有权限完结流程
     *
     * @return
     */
    public Boolean isLastPoint(Integer currentStep) {
        return currentStep.equals(getLastPoint().getStep());
    }

    /**
     * 获取审批的最后节点
     *
     * @return
     */
    public MmFlowConfigItem getFirstPoint() {
        return CONFIG_ITEM_THREAD_LOCAL.get().get(0);
    }

    /**
     * 获取审批的最后节点
     *
     * @return
     */
    public MmFlowConfigItem getLastPoint() {
        List<MmFlowConfigItem> flows = CONFIG_ITEM_THREAD_LOCAL.get();
        return flows.get(flows.size() - 1);
    }

    /**
     * 判断当前用户是否有权限完结流程
     *
     * @return
     */
    public Boolean containsLastStepRole() {
        return JwtUtils.getRoles().contains(getLastPoint().getRoleName());
    }

    public MmFlowConfigItem getStep(Integer step) {
        List<MmFlowConfigItem> flows = CONFIG_ITEM_THREAD_LOCAL.get();
        int index;
        if (step <= 0) {
            index = 0;
        } else if (step > flows.size()) {
            index = flows.size() - 1;
        } else {
            index = step - 1;
        }
        return flows.get(index);
    }

    public Boolean hasPrivilege(String role) {
        return JwtUtils.getRoles().contains(role);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        resetConfigMap();
    }
}
