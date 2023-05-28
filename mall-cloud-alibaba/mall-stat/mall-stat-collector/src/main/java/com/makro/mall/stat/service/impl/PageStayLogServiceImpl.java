package com.makro.mall.stat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.mapper.PageStayLogMapper;
import com.makro.mall.stat.pojo.metadata.PageStayLog;
import com.makro.mall.stat.service.PageStayLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class PageStayLogServiceImpl extends ServiceImpl<PageStayLogMapper, PageStayLog>
        implements PageStayLogService {

    @Override
    public List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime) {
        return getBaseMapper().getCustomerExportDTO(mmCode, startTime, endTime);
    }
}




