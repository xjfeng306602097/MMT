package com.makro.mall.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.makro.mall.admin.pojo.entity.MmCustomer;
import com.makro.mall.admin.pojo.vo.MmCustomerPageReqVO;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.admin.pojo.vo.VerifyCustomerRepVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * @author Ljj
 * @description 针对表【MM_CUSTOMER(客户表)】的数据库操作Service
 * @createDate 2022-05-12 16:19:44
 */
public interface MmCustomerService extends IService<MmCustomer> {
    /**
     * 功能描述: 分页查询
     * 客户资料上传&Segment维护
     *
     * @Author: 卢嘉俊
     * @Date: 2022/5/12
     */
    IPage<MmCustomerVO> page(Page<MmCustomerVO> page, MmCustomerPageReqVO customer, String sortSql);

    /**
     * 功能描述: 新增客户或按手机号码更新用户
     * 如用户手机号已存在则为修改
     * 客户资料上传&Segment维护
     *
     * @Author: 卢嘉俊
     * @Date: 2022/5/12
     */
    void createOrUpdateBatch(List<MmCustomerVO> customers);

    List<MmCustomer> getMmCustomerBySegmentId(Set<Long> segmentId);

    MmCustomerVO getMmCustomerVOByPhone(String phone);

    MmCustomerVO getMmCustomerVOById(Long id);

    Boolean delete(String ids);

    /**
     * 功能描述: 根据id更新用户
     * 客户资料上传&Segment维护
     *
     * @Author: 卢嘉俊
     * @Date: 2022/5/14
     */
    void updateCustomer(MmCustomerVO customer);

    /**
     * 功能描述: 解析excel
     * 客户资料上传&Segment维护
     *
     * @Param: excel
     * @Author: 卢嘉俊
     * @Date: 2022/5/16
     */
    List<MmCustomerVO> parseExcel(MultipartFile file, Long segmentId, String lineBotChannelToken);

    void customerValidated(List<MmCustomerVO> customers);


    VerifyCustomerRepVO verifyCustomer(String data, String field);

    Boolean deleteSegment(String ids, String segmentId);

    MmCustomerVO getVOByLineId(String lineUserId);

    MmCustomerVO getMmCustomerVOByCode(String str);

    List<MmCustomerVO> getMmCustomerVOByIds(List<String> customerIds);

}
