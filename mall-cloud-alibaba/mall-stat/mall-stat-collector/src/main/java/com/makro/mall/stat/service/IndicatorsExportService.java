package com.makro.mall.stat.service;


import com.makro.mall.stat.pojo.dto.MixedPanelSummaryReqDTO;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

public interface IndicatorsExportService {
    void exportExcel(String mmCode, Date startTime, Date endTime, HttpServletResponse response);

    void exportMixedPanelSummary(MixedPanelSummaryReqDTO dto, HttpServletResponse response);
}
