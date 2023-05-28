package com.makro.mall.stat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.pojo.metadata.PageStayLog;

import java.util.List;

/**
 *
 */
public interface PageStayLogService extends IService<PageStayLog> {

    List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime);
}
