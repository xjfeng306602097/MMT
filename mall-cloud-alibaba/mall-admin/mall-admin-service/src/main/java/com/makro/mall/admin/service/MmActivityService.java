package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.dto.MakroMailPageReq;
import com.makro.mall.admin.pojo.dto.MmActivityPageRepDTO;
import com.makro.mall.admin.pojo.dto.MmActivityPageReqDTO;
import com.makro.mall.admin.pojo.entity.MmActivity;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmActivityAppPageRepVO;
import com.makro.mall.admin.pojo.vo.MmActivityBatchReqVO;
import com.makro.mall.admin.pojo.vo.MmActivityVO;
import com.makro.mall.common.model.MakroPage;
import com.makro.mall.common.model.SortPageRequest;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 *
 */
public interface MmActivityService extends IService<MmActivity> {


    /**
     * 用户分页列表
     */
    IPage<MmActivity> list(Page<MmActivity> page, MmActivityPageReqDTO dto);

    /**
     * 回滚发布
     */
    Boolean rollBack(String mmCode, Long status);

    MmActivityVO detail(Integer id);

    MmActivityVO getByCode(String mmCode);

    /**
     * 功能描述: 根据MM_Activity的segment查询对应Customer
     * Mail发布工程
     *
     * @Author: 卢嘉俊
     * @Date: 2022/5/30
     */
    List<MmCustomer> getPublishUsers(String mmCode, String filter);

    /**
     * 功能描述:
     * mmapp增加缓存
     *
     * @Param: key 缓存key
     * @Author: 卢嘉俊
     * @Date: 2022/6/15
     */
    MakroPage<MmActivityAppPageRepVO> appPage(SortPageRequest<MakroMailPageReq> request);

    /**
     * 通过本地缓存拉取数据
     */
    MakroPage<MmActivityAppPageRepVO> appPageFromCache(SortPageRequest<MakroMailPageReq> request) throws ExecutionException;

    /**
     * 清理缓存
     */
    void cleanUpCache();

    /**
     * 功能描述:
     * 这个每小时扫表，扫publishStatus=1的记录，然后检查它的结束时间
     * 如果endTime小于当前时间了，直接将mmActivity的publishStatus改为2
     *
     * @Author: 卢嘉俊
     * @Date: 2022/7/19 活动结束定时调度
     */
    Boolean scanMmActivityForFailure();

    IPage<MmActivityPageRepDTO> getPage(MmActivityPageReqDTO dto);

    void add(MmActivity activity);

    Boolean saveWithProduct(MmActivityBatchReqVO vo);

    Boolean delete(String ids);
}
