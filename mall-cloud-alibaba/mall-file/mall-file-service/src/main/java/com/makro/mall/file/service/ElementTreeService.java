package com.makro.mall.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.file.pojo.entity.MmElementTree;
import com.makro.mall.file.pojo.vo.ElementTreeListReqVO;
import com.makro.mall.file.pojo.vo.ElementTreeListRspVO;
import com.makro.mall.file.pojo.vo.ElementTreeUpDateReqVO;

import java.util.List;

/**
 * @author jincheng
 * @description 针对表【MM_ELEMENT_TREE】的数据库操作Service
 * @createDate 2022-06-27 17:23:47
 */
public interface ElementTreeService extends IService<MmElementTree> {

    ElementTreeListRspVO treeList(ElementTreeListReqVO vo);

    Boolean saveTree(List<ElementTreeUpDateReqVO> element);

    Boolean updateTree(ElementTreeUpDateReqVO element);
}
