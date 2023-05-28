package com.makro.mall.product.Listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.vo.ProdStorageVO;
import com.makro.mall.product.service.ProdStorageService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class ProdStorageExcelListener extends AnalysisEventListener<ProdStorageVO> {


    private final List<ProdStorageVO> list = new ArrayList<>();
    private ProdStorageService prodStorageService;
    private String segmentName;


    // 一行一行去读取excel中的内容(表头不会去读取)
    @Override
    public void invoke(ProdStorageVO storage, AnalysisContext context) {

        log.info("商品excel解析到一条数据:{}", JSON.toJSONString(storage));
        //参数校验
        String itemCode = storage.getItemcode();
        if (StrUtil.isBlank(itemCode)) {
            log.error("商品excel解析到数据错误-itemCode为空:{}", JSON.toJSONString(storage));
            return;
        }
        //判断是否存在
        boolean exists = prodStorageService.getBaseMapper().exists(new LambdaQueryWrapper<ProdStorage>().eq(ProdStorage::getItemcode, storage.getItemcode()));
        storage.setId(storage.getItemcode().trim());
        storage.setIsvalid(1);
        storage.setIsExist(exists);
        storage.setSegmentName(segmentName);

        log.info("商品excel添加一条数据到列表:{}", JSON.toJSONString(storage));
        list.add(storage);

    }


    // 读取完成之后做的内容
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("所有商品excel数据解析完成！");
    }
}
