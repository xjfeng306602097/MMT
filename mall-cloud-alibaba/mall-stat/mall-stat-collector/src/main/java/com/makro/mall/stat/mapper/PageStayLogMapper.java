package com.makro.mall.stat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.makro.mall.stat.manager.MmCustomerExportDTO;
import com.makro.mall.stat.pojo.metadata.PageStayLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Entity com.makro.mall.stat.pojo.metadata.PageStayLog
 */
@Mapper
public interface PageStayLogMapper extends BaseMapper<PageStayLog> {

    List<MmCustomerExportDTO> getCustomerExportDTO(String mmCode, String startTime, String endTime);
}




