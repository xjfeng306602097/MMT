package com.makro.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.product.mapper.ProdPicMapper;
import com.makro.mall.product.pojo.entity.ProdPic;
import com.makro.mall.product.service.ProdPicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProdPicServiceImpl extends ServiceImpl<ProdPicMapper, ProdPic>
        implements ProdPicService {

    @Override
    public List<ProdPic> listRelatedPicById(Long id) {
        return this.baseMapper.listRelatedPicById(id);
    }

    @Override
    @Transactional
    public Boolean add(ProdPic pic) {
        // 1.先新增商品
        if (save(pic) && Objects.nonNull(pic.getItemCode()) && Objects.nonNull(pic.getFilePath())) {
            // 新增的图片为选中默认,其他图片需要调整为
            update(new LambdaUpdateWrapper<ProdPic>().eq(ProdPic::getItemCode, pic.getItemCode())
                    .ne(ProdPic::getId, pic.getId())
                    .set(ProdPic::getDefaulted, 0));
            update(new LambdaUpdateWrapper<ProdPic>().eq(ProdPic::getItemCode, pic.getItemCode())
                    .eq(ProdPic::getId, pic.getId())
                    .set(ProdPic::getDefaulted, 1));
            log.info("新增图片成功{},重置其他图片为非默认并设置当前图片为默认", pic.getId());
        }
        return true;
    }
}




