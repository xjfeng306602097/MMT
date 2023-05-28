package com.makro.mall.admin.component.easyexcel.Listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.makro.mall.admin.pojo.dto.MmCustomerDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ljj
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class CustomerExcelListener extends AnalysisEventListener<MmCustomerDTO> {


    private final List<MmCustomerDTO> list = new ArrayList<>();


    /**
     * 一行一行去读取excel中的内容(表头不会去读取)
     */
    @Override
    public void invoke(MmCustomerDTO data, AnalysisContext context) {
        if (data.getPhone() == null) {
            log.error("客户excel解析到数据错误:{}", data);
            return;
        }
        if (StrUtil.isBlank(data.getName())){
            data.setName(data.getCustomerCode());
        }
        list.add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

}
