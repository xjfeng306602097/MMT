package com.makro.mall.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.common.constant.SystemConstants;
import com.makro.mall.admin.mapper.SysDeptMapper;
import com.makro.mall.admin.pojo.entity.SysDept;
import com.makro.mall.admin.pojo.vo.DeptVO;
import com.makro.mall.admin.pojo.vo.TreeSelectVO;
import com.makro.mall.admin.service.SysDeptService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
        implements SysDeptService {

    @Override
    public List<DeptVO> listTable(Integer status, String name, Boolean hasChild) {
        List<SysDept> deptList = this.list(new LambdaQueryWrapper<SysDept>()
                .like(StrUtil.isNotBlank(name), SysDept::getName, name)
                .orderByAsc(SysDept::getSort));
        hasChild = Optional.ofNullable(hasChild).orElse(Boolean.TRUE);
        if (hasChild) {
            return recursionTableList(SystemConstants.ROOT_DEPT_ID, deptList);
        } else {
            return deptList.stream().map(m -> BeanUtil.copyProperties(m, DeptVO.class)).collect(Collectors.toList());
        }
    }

    @Override
    public List<TreeSelectVO> listTreeSelect() {
        List<SysDept> deptList = this.list(new LambdaQueryWrapper<SysDept>()
                .orderByAsc(SysDept::getSort));
        return recursionTreeSelect(SystemConstants.ROOT_DEPT_ID, deptList);
    }

    @Override
    @Transactional
    public Long saveDept(SysDept dept) {
        String treePath = getDeptTreePath(dept.getParentId());
        dept.setTreePath(treePath);
        this.saveOrUpdate(dept);
        return dept.getId();
    }

    @Override
    @Transactional
    public boolean deleteByIds(String ids) {
        AtomicBoolean status = new AtomicBoolean(false);
        List<String> idList = Arrays.asList(ids.split(","));
        Optional.ofNullable(idList).orElse(new ArrayList<>()).forEach(id -> {
            status.set(this.remove(new LambdaQueryWrapper<SysDept>().eq(SysDept::getId, id)
                    .or()
                    .apply("CONCAT(CONCAT(',', tree_path),',') like CONCAT(CONCAT('%', {0}),'%')", id)));
        });
        return status.get();
    }

    private String getDeptTreePath(Long parentId) {
        String treePath;
        parentId = Optional.ofNullable(parentId).orElse(Long.valueOf(0));
        if (parentId.equals(SystemConstants.ROOT_DEPT_ID)) {
            treePath = String.valueOf(SystemConstants.ROOT_DEPT_ID);
        } else {
            SysDept parentDept = this.getById(parentId);
            treePath = Optional.ofNullable(parentDept).map(item -> item.getTreePath() + "," + item.getId()).orElse(Strings.EMPTY);
        }
        return treePath;
    }

    private List<DeptVO> recursionTableList(Long parentId, List<SysDept> deptList) {
        List<DeptVO> deptTableList = new ArrayList<>();
        Optional.ofNullable(deptList).orElse(new ArrayList<>())
                .stream().filter(dept -> dept.getParentId().equals(parentId))
                .forEach(dept -> {
                    DeptVO deptVO = new DeptVO();
                    BeanUtil.copyProperties(dept, deptVO);
                    List<DeptVO> children = recursionTableList(dept.getId(), deptList);
                    deptVO.setChildren(children);
                    deptTableList.add(deptVO);
                });
        return deptTableList;
    }

    private List<TreeSelectVO> recursionTreeSelect(Long parentId, List<SysDept> deptList) {
        List<TreeSelectVO> treeSelectList = new ArrayList<>();
        Optional.ofNullable(deptList).orElse(new ArrayList<>())
                .stream().filter(dept -> dept.getParentId().equals(parentId))
                .forEach(dept -> {
                    TreeSelectVO deptVO = new TreeSelectVO();
                    deptVO.setId(dept.getId());
                    deptVO.setName(dept.getName());
                    List<TreeSelectVO> children = recursionTreeSelect(dept.getId(), deptList);
                    deptVO.setChildren(children);
                    treeSelectList.add(deptVO);
                });
        return treeSelectList;
    }
}




