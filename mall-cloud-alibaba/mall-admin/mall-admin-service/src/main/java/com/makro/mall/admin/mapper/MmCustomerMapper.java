package com.makro.mall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmCustomerPageReqVO;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * @author Ljj
 * @description 针对表【MM_CUSTOMER(客户表)】的数据库操作Mapper
 * @createDate 2022-05-12 16:19:44
 * @Entity com.makro.mall.admin.pojo.entity.MmCustomer
 */
@Mapper
public interface MmCustomerMapper extends BaseMapper<MmCustomer> {

    List<MmCustomer> list(Page<MmCustomerVO> page, MmCustomerPageReqVO customer, Set<Long> segments, String sortSql, Set<String> memberTypeIds);

    Long maxId();
}




