package com.makro.mall.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.vo.ProductVO;

import java.util.List;

/**
 * @author Thierry.Zhuang
 * @description 针对表【PROD_LIST(添加或导入模板里商品设计相关数据，MM商品明细的来源)】的数据库操作Service
 * @createDate 2022-04-14 13:08:41
 */
public interface ProdListService extends IService<ProdList> {

    void saveBatchProdList(List<ProdList> list);

    List<ProdList> linkItemList(String itemCode, String mmCode, Integer isvalid);

    List<String> getPages(String mmCode);

    IPage<ProductVO> selectList(Integer page, Integer limit, String itemcode, String namethai, String mmCode, String infoid, String channelType, Integer isvalid, Integer mmpage, Integer mmsort, String nameen, Long segmentId, List<String> join, Long productId);

    ProductVO getProductVO(ProdList d, List<String> o, List<ProdList> dataList);

    ProductVO getProductVO(ProdList d, List<String> o);

    ProductVO getProductVO(ProdList d);

    Boolean add(String mmCode, ProdList data);

    Boolean updateForProd(String id, ProdList data);

    List<ProductVO> listByIdsForProd(String ids);

    IPage<MmActivity> getMmActivityByItemCode(String itemCode, Long page, Long limit);

    BaseResponse<String> getMmCodeByItemCode(String itemCode);

    Boolean parentIdScript();

    Boolean updateValid(String id, Integer i);

    Boolean updatebyItemCode(String mmCode, String itemCode, String parentCode, ProdList data);

    Boolean updateAllForProd(String id, ProdList data);
}
