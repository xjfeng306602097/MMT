package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.SysDept;
import com.makro.mall.admin.pojo.vo.DeptVO;
import com.makro.mall.admin.pojo.vo.TreeSelectVO;

import java.util.List;

/**
 *
 */
public interface SysDeptService extends IService<SysDept> {

    List<DeptVO> listTable(Integer status, String name, Boolean hasChild);

    List<TreeSelectVO> listTreeSelect();

    Long saveDept(SysDept dept);

    boolean deleteByIds(String ids);
}
