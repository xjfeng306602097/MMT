package com.makro.mall.admin.pojo.vo;

import com.makro.mall.admin.pojo.entity.MmFlow;
import com.makro.mall.admin.pojo.entity.MmFlowDetails;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/5/9
 */
@Data
@AllArgsConstructor
public class MmFlowDetailsVO {

    private MmFlow flow;

    private List<MmFlowDetails> details = new ArrayList<>();

}
