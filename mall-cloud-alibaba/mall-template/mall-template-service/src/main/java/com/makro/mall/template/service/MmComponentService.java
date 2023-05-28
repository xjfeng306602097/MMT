package com.makro.mall.template.service;

import com.makro.mall.template.pojo.entity.MmComponent;
import com.makro.mall.template.pojo.entity.MmComponentDraft;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description 组件服务
 * @date 2021/10/28
 */
public interface MmComponentService {
    MmComponent save(MmComponent mmComponent);

    MmComponent update(MmComponent mmComponent);

    Page<MmComponent> page(List<Integer> status, String name, Date begin, Date end,
                           Integer page, Integer size, Integer isDelete);

    MmComponent getByCode(String code);

    Long removeByCodes(List<String> codes);

    MmComponentDraft getDraft(String code, Integer version);

    List<MmComponentDraft> getDraftInfos(String code);
}
