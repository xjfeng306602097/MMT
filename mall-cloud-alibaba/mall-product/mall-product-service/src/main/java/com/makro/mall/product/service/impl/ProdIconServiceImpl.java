package com.makro.mall.product.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.ProductStatusCode;
import com.makro.mall.product.mapper.ProdIconMapper;
import com.makro.mall.product.pojo.entity.ProdIcon;
import com.makro.mall.product.pojo.entity.ProdIconPic;
import com.makro.mall.product.service.ProdIconPicService;
import com.makro.mall.product.service.ProdIconService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 *
 */
@Service
@RequiredArgsConstructor
public class ProdIconServiceImpl extends ServiceImpl<ProdIconMapper, ProdIcon>
        implements ProdIconService {
    private final ProdIconPicService iconPicService;

    @Override
    public List<String> getIconNames() {
        return this.baseMapper.getIconNames();
    }

    @Override
    public IPage<ProdIcon> list(Page<ProdIcon> page, ProdIcon icon) {
        String name = icon.getName();
        Integer isvalid = icon.getIsvalid();
        LambdaQueryWrapper<ProdIcon> qw = new LambdaQueryWrapper<ProdIcon>()
                .eq(isvalid != null, ProdIcon::getIsvalid, isvalid)
                .eq(StrUtil.isNotBlank(name), ProdIcon::getName, name)
                .orderByDesc(ProdIcon::getGmtCreate);
        return this.baseMapper.selectPage(page, qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(String id, ProdIcon icon) {
        icon.setId(id);
        Boolean result = updateById(icon);

        if (icon.getPicid() != null) {
            changeDefault(id, icon);
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updatePic(String id, ProdIconPic pic) {
        pic.setId(id);
        String iconId = pic.getIconid();
        if (pic.getDefaulted() != null) {
            changeDefault(id, pic, iconId);
        }
        return iconPicService.updateById(pic);
    }

    /**
     * 功能描述: PROD_ICON_PIC设置默认处理逻辑
     *
     * @Param: iconId = PROD_ICON 的ID
     * @Param: id = PROD_ICON_PIC 的ID
     * @Author: 卢嘉俊
     * @Date: 2022/6/20 处理空指针以及全表扫描隐患
     */
    private void changeDefault(String id, ProdIconPic pic, String iconId) {
        //iconId不能为空
        Assert.isTrue(Objects.nonNull(iconId), ProductStatusCode.ICONID_IS_EMPTY);

        // 检查是否存在默认图片
        List<ProdIconPic> picList = iconPicService.list(new LambdaQueryWrapper<ProdIconPic>().eq(ProdIconPic::getIconid, iconId));
        if (Objects.equals(1L, pic.getDefaulted())) {
            // 调为默认
            for (ProdIconPic prodIconPic : picList) {
                if (prodIconPic.getDefaulted() == 1L && !prodIconPic.getId().equals(id)) {
                    prodIconPic.setDefaulted(0L);
                    // 直接跳出，只允许一条记录更新
                    iconPicService.updateById(prodIconPic);
                }
            }
            update(new LambdaUpdateWrapper<ProdIcon>()
                    .eq(ProdIcon::getId, iconId)
                    .set(ProdIcon::getPicid, pic.getId()));
        } else {
            // 检查是否存在默认图片
            Assert.isTrue(picList.stream().anyMatch(p -> p.getDefaulted() == 1L && !p.getId()
                    .equals(pic.getId())), ProductStatusCode.ICON_PICTURE_DEFAULTED_REQUIRED);
        }
    }

    /**
     * 功能描述: PROD_ICON设置默认处理逻辑 在更新ProdIcon的picId时触发
     * 1.将iconId内的默认取消
     * 2.将当前图片设置为默认
     *
     * @Param: id = PROD_ICON 的ID = PROD_ICON_PIC的iconId
     * @Author: 卢嘉俊
     * @Date: 2022/6/20
     */
    private void changeDefault(String id, ProdIcon icon) {
        iconPicService.update(new LambdaUpdateWrapper<ProdIconPic>().eq(ProdIconPic::getIconid, id).eq(ProdIconPic::getDefaulted, 1L).set(ProdIconPic::getDefaulted, 0L));
        iconPicService.update(new LambdaUpdateWrapper<ProdIconPic>().eq(ProdIconPic::getId, icon.getPicid()).set(ProdIconPic::getDefaulted, 1L));
    }
}




