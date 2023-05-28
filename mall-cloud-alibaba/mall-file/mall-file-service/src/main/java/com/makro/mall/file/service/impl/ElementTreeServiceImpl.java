package com.makro.mall.file.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.FileStatusCode;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.file.mapper.ElementTreeMapper;
import com.makro.mall.file.pojo.entity.MmElement;
import com.makro.mall.file.pojo.entity.MmElementTree;
import com.makro.mall.file.pojo.vo.*;
import com.makro.mall.file.service.ElementTreeService;
import com.makro.mall.file.service.MmElementService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jincheng
 * @description 针对表【MM_ELEMENT_TREE】的数据库操作Service实现
 * @createDate 2022-06-27 17:23:47
 */
@Service
@RequiredArgsConstructor
public class ElementTreeServiceImpl extends ServiceImpl<ElementTreeMapper, MmElementTree>
        implements ElementTreeService {

    public static final String PUBLIC = "PUBLIC";
    private final MmElementService mmElementService;

    @Override
    public ElementTreeListRspVO treeList(ElementTreeListReqVO vo) {
        Long id = vo.getId();
        ElementTreeListRspVO elementTreeListVO = new ElementTreeListRspVO();
        //获取当前用户所有数据
        List<ElementTreeVO> elementTreeList = getTreePage(vo);
        //获取上一个文件夹
        if (!Objects.equals(id, 0L)) {
            elementTreeListVO.setPreviousId(getById(id).getParentId());
        } else {
            elementTreeListVO.setPreviousId(0L);
        }
        elementTreeListVO.setList(elementTreeList);
        return elementTreeListVO;
    }

    /**
     * 功能描述: 获取当前用户所有数据
     *
     * @Author: 卢嘉俊
     * @Date: 2022/6/29 element文件夹
     */
    private List<ElementTreeVO> getTreePage(ElementTreeListReqVO req) {
        return list(new LambdaQueryWrapper<MmElementTree>()
                .in(MmElementTree::getUserId, JwtUtils.getUsername(), PUBLIC)
                .eq(MmElementTree::getParentId, req.getId())
                .eq(StrUtil.isNotBlank(req.getType()), MmElementTree::getType, req.getType()))
                .stream().map(x -> {
                    ElementTreeVO vo = new ElementTreeVO();
                    BeanUtils.copyProperties(x, vo);
                    if (!Objects.equals(x.getElementId(), 0L)) {
                        //封装元素
                        MmElement element = mmElementService.getById(x.getElementId());
                        if (Objects.isNull(element)) {
                            return null;
                        }
                        vo.setElementVO(MmElementVO.convert(element));
                    }
                    vo.setType(Objects.equals(x.getElementId(), 0L) ? "0" : "1");
                    vo.setIsPublic(StrUtil.equals(x.getUserId(), PUBLIC));
                    return vo;
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveTree(List<ElementTreeUpDateReqVO> element) {
        String username = JwtUtils.getUsername();
        //公有
        element.stream().filter(ElementTreeUpDateReqVO::getIsPublic).forEach(x -> {
            validSave(x);
            x.setUserId(PUBLIC);
            x.setType(x.getElementType());
            save(x);
        });
        //私有
        element.stream().filter(x -> (!x.getIsPublic())).forEach(x -> {
            validSave(x);
            x.setUserId(String.valueOf(username));
            x.setType(x.getElementType());
            save(x);
        });

        return true;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateTree(ElementTreeUpDateReqVO element) {
        String username;
        if (element.getIsPublic()) {
            username = "";
        } else {
            username = JwtUtils.getUsername();
        }
        validSave(element);
        element.setUserId(username);
        return updateById(element);
    }

    private void validSave(ElementTreeUpDateReqVO element) {
        if (Objects.equals(element.getElementId(), 0L)) {
            //文件夹名字不为空且不允许有同名文件夹
            Assert.notNull(element.getName(), FileStatusCode.WRONG_FOLDER_NAME);
            MmElementTree one = getOne(new LambdaQueryWrapper<MmElementTree>().select(MmElementTree::getId)
                    .in(MmElementTree::getUserId, JwtUtils.getUsername(), PUBLIC)
                    .eq(MmElementTree::getParentId, element.getParentId())
                    .eq(MmElementTree::getName, element.getName())
                    .eq(StrUtil.isNotBlank(element.getType()), MmElementTree::getType, element.getType()));
            Assert.isNull(one, FileStatusCode.WRONG_FOLDER_NAME);
        }
    }


}




