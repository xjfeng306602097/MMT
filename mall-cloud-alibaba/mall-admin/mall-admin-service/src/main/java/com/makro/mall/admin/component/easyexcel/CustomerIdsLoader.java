package com.makro.mall.admin.component.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.makro.mall.file.api.FileFeignClient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/3/10
 */
@Component
@AllArgsConstructor
@Slf4j
public class CustomerIdsLoader {

    private final FileFeignClient fileFeignClient;

    public Set<String> getCustomerIdsFromExcel(String url) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return new HashSet<String>();
        }
        Set<String> set = new HashSet<>();
        String suffix = url.substring(url.lastIndexOf(".") + 1, url.length());
        try (InputStream ins = new ByteArrayInputStream(fileFeignClient.getObjectStream("makro-excel", url.substring(url.lastIndexOf("/"))).getData())) {
            EasyExcel.read(ins, CustomerInfo.class, new AnalysisEventListener<CustomerInfo>() {
                @Override
                public void invoke(CustomerInfo info, AnalysisContext analysisContext) {
                    set.add(info.getCustomerCode());
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    log.info("File:{} import customers successfully, total count:{}", url, set.size());
                }
            }).excelType("xlsx".equals(suffix) ? ExcelTypeEnum.XLSX : ("xls".equals(suffix) ? ExcelTypeEnum.XLS : ExcelTypeEnum.CSV)).sheet(0).doRead();
        }
        return set;
    }

    @Data
    public static class CustomerInfo {

        @ExcelProperty("code")
        private String customerCode;

    }

}
