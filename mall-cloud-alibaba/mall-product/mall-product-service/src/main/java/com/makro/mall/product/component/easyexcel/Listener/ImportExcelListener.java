package com.makro.mall.product.component.easyexcel.Listener;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.fastjson2.JSON;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.product.config.NacosConfig;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import com.makro.mall.product.pojo.vo.ExcelDataVO;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ljj
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ImportExcelListener extends AnalysisEventListener<ExcelDataVO> {

    /**
     * 模板主表Id
     */
    private NacosConfig nacosConfig;
    /**
     * 统计行数
     */
    private int rowNum = 0;
    /**
     * 警告信息
     */
    private String wrongFormatMsg = "Correct";
    /**
     * 表头前几行的文字
     */
    private String headInfo = "";
    private String filePath;
    private ExcelDataFromSheetName excelDataFromSheetName;
    private List<ExcelDataVO> templateDataVOList = new ArrayList<>();


    public ImportExcelListener(NacosConfig nacosConfig, String filePath) {
        this.nacosConfig = nacosConfig;
        this.filePath = filePath;
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        int index = context.readRowHolder().getRowIndex();
        if (index + 1 <= nacosConfig.getHeadRowNum() - 2) {
            headInfo += JSON.toJSONString(headMap);
        }
        if (index == nacosConfig.getHeadRowNum()) {
            // 从Nacos配置项获取第N行简单判断是否标题行表头是否相同
            nacosConfig.columns().forEach((k, v) -> {
                String excelColumnName = headMap.get(k);
                if (!StrUtil.containsIgnoreCase(excelColumnName, v)) {
                    log.info(JSON.toJSONString(headMap));
                    wrongFormatMsg = " --  Inconsistent from the column: " + excelColumnName;
                    throw new BusinessException(StatusCode.EXCEL_FORMAT_ERROR.message(wrongFormatMsg));
                }
            });
        }
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        // 所有的异常都经过EasyExcel处理，需要前端交互选择将其抛出BusinessException，无需则就地catch解决
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) exception;
            int row = excelDataConvertException.getRowIndex() + 1;
            int col = excelDataConvertException.getColumnIndex() + 1;
            wrongFormatMsg = "Row " + row + " Column " + col + " [" + excelDataConvertException.getCellData() + "] Wrong Data Format.";
        }
        throw new BusinessException(wrongFormatMsg);
    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return "Correct".equals(wrongFormatMsg);
    }

    @Override
    public void invoke(ExcelDataVO templateDataVO, AnalysisContext analysisContext) {
        int row = analysisContext.readRowHolder().getRowIndex() + 1;
        templateDataVO.setRow(row);
        templateDataVOList.add(templateDataVO);
        // 获取Nacos配置的必填列进行检验
        List<Integer> requiredfield = nacosConfig.getRequiredfield();
        for (Integer f : requiredfield) {
            String columnName = nacosConfig.columns().get(f);
            CellData c = (CellData) analysisContext.readRowHolder().getCellMap().get(f);
            if (c == null || c.getType() == CellDataTypeEnum.EMPTY) {
                wrongFormatMsg = ": Please confirm whether item in Row " + row + " is to be used." +
                        "\n If it's valid, Column '" + columnName + "' can not be empty." +
                        "\n If not used, delete that line.";
                throw new BusinessException(StatusCode.EXCEL_FORMAT_ERROR.message(wrongFormatMsg));
            }
        }
        rowNum++;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        ProdTemplateInfo info = new ProdTemplateInfo();
        String sheetName = analysisContext.readSheetHolder().getSheetName();
        info.setId(IdUtil.simpleUUID());
        info.setPromoinfo(headInfo);
        info.setSheetname(sheetName);
        info.setImportresult(wrongFormatMsg);
        info.setDatanum((long) rowNum);
        info.setFilename(filePath);
        info.setCreator(JwtUtils.getUsername());
        info.setIsvalid(1);
        info.setStatus(1);
        excelDataFromSheetName = new ExcelDataFromSheetName(info, templateDataVOList);
    }


}
