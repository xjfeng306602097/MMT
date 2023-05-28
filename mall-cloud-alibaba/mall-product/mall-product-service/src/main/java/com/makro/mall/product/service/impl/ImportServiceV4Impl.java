package com.makro.mall.product.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.BaseResponse;
import com.makro.mall.common.model.BusinessException;
import com.makro.mall.common.model.StatusCode;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.common.web.bean.MakroMultipartFile;
import com.makro.mall.common.web.util.JwtUtils;
import com.makro.mall.file.api.FileFeignClient;
import com.makro.mall.file.pojo.dto.StreamDTO;
import com.makro.mall.file.pojo.dto.UploadResultDTO;
import com.makro.mall.product.component.easyexcel.Listener.ImportExcelListener;
import com.makro.mall.product.config.NacosConfig;
import com.makro.mall.product.manager.ImportManager;
import com.makro.mall.product.pojo.dto.*;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.product.pojo.entity.ProdPic;
import com.makro.mall.product.pojo.entity.ProdStorage;
import com.makro.mall.product.pojo.entity.ProdTemplateInfo;
import com.makro.mall.product.pojo.vo.ExcelDataFromSheetName;
import com.makro.mall.product.pojo.vo.ExcelDataVO;
import com.makro.mall.product.pojo.vo.ProductVO;
import com.makro.mall.product.pojo.vo.SheetNameVo;
import com.makro.mall.product.service.ImportV4Service;
import com.makro.mall.product.service.ProdListService;
import com.makro.mall.product.service.ProdPicService;
import com.makro.mall.product.service.ProdStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author Ljj
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImportServiceV4Impl implements ImportV4Service {


    private final FileFeignClient fileFeignClient;
    private final RedisUtils redisUtil;
    private final ProdListService dataService;
    private final ProdStorageService storageService;
    private final ProdPicService prodPicService;
    private final ImportManager importManager;
    private final NacosConfig nacosConfig;

    @Override
    public SheetNameVo getSheetList(MultipartFile file) {
        try (InputStream ins = file.getInputStream()) {
            String uploadId = IdUtil.simpleUUID();

            ExcelReader excelReader = EasyExcel.read(ins).build();

            List<String> sheetNames = excelReader.excelExecutor().sheetList().stream().map(ReadSheet::getSheetName).collect(Collectors.toList());

            //上传文件
            String path = uploadFileToMinio(file);

            // key uploadId value 文件地址
            String key = RedisConstants.TEXT_THAI_IMPORT_PREFIX + uploadId;
            redisUtil.set(key, path, 24 * 60 * 60);

            return new SheetNameVo(sheetNames, uploadId);
        } catch (IOException e) {
            log.error(StatusCode.FILE_ERROR.getMsg(), e);
            throw new BusinessException(StatusCode.FILE_ERROR);
        }
    }

    @Override
    public ExcelDataDTO getExcelDataFromSheetName(String uploadId, String sheetName) {
        String filePath = (String) redisUtil.get(RedisConstants.TEXT_THAI_IMPORT_PREFIX + uploadId);
        Assert.isTrue(StrUtil.isNotBlank(filePath), StatusCode.FILE_ERROR);

        try (InputStream ins = new ByteArrayInputStream(fileFeignClient.getObjectStream("product", filePath.substring(filePath.lastIndexOf("/"))).getData())) {
            ImportExcelListener importExcelListener = new ImportExcelListener(nacosConfig, filePath);
            EasyExcel.read(ins, ExcelDataVO.class, importExcelListener).sheet(sheetName).headRowNumber(nacosConfig.getHeadRowNum() + 1).doRead();

            ExcelDataFromSheetName excelData = importExcelListener.getExcelDataFromSheetName();

            List<ProdList> dataList = new ArrayList<>();
            List<LinkItemDTO> linkItems = new ArrayList<>();
            List<IndexV4DTO> indexList = new ArrayList<>();
            String infoId = excelData.getInfo().getId();
            String username = excelData.getInfo().getCreator();
            List<String> itemCodes = excelData.getTemplateDataVOList().stream().map(ExcelDataVO::getUrlparam).collect(Collectors.toList());

            //查询图片库
            for (int i = 0; i < excelData.getTemplateDataVOList().size(); i++) {
                ExcelDataVO templateDataVO = excelData.getTemplateDataVOList().get(i);

                //整理主商品 和 关联商品
                insertProdList(dataList, linkItems, infoId, username, templateDataVO);

                //封装行数
                indexList.add(new IndexV4DTO(i, templateDataVO.getRow()));
            }


            // 关联商品处理
            linkItemsHandle(dataList, linkItems, itemCodes, infoId, username);

            //设置图片 父ID
            List<ProdListV4DTO> prodListV4DTOList = insertPicAndParentId(dataList);

            return new ExcelDataDTO(importExcelListener.getExcelDataFromSheetName().getInfo(), prodListV4DTOList, indexList);
        } catch (IOException e) {
            log.error("getExcelDataFromSheetName 导入失败" + StatusCode.EXCEL_FORMAT_ERROR.getMsg(), e);
            throw new BusinessException(StatusCode.EXCEL_FORMAT_ERROR);
        }
    }

    private List<ProdListV4DTO> insertPicAndParentId(List<ProdList> dataList) {
        //将pic组装成map
        Map<String, ProdPic> picMap = packagePicMap(dataList);

        //设置图片
        return dataList.stream().map(x -> {
            ProdListV4DTO prodListV4DTO = new ProdListV4DTO();
            BeanUtil.copyProperties(x, prodListV4DTO);
            setParentId(dataList, prodListV4DTO);
            setPic(prodListV4DTO, picMap);
            return prodListV4DTO;
        }).collect(Collectors.toList());
    }

    private void setParentId(List<ProdList> dataList, ProdListV4DTO x) {
        //存进后 找出关联商品填充其parentId
        if (StrUtil.isNotBlank(x.getParentCode())) {
            //从商品列表查出
            for (ProdList father : dataList) {
                if (ObjectUtil.equals(father.getUrlparam(), x.getParentCode())) {
                    x.setParentId(father.getId());
                    return;
                }
            }
        }
    }

    private void insertProdList(List<ProdList> dataList, List<LinkItemDTO> linkItems, String infoId, String username, ExcelDataVO templateDataVO) {
        // 保存Excel商品主体信息
        ProdList data = new ProdList();
        data.setId(IdUtil.simpleUUID());
        data.setCreator(username);
        data.setInfoid(infoId);
        data.setIsvalid(1);
        BeanUtil.copyProperties(templateDataVO, data);
        if (StrUtil.isNotBlank(data.getChannelType())) {
            data.setChannelType(StrUtil.trim(data.getChannelType().toLowerCase()));
        }
        String giftCodesStr = templateDataVO.getLinkitemno();
        if (StrUtil.isNotBlank(giftCodesStr)) {
            Set<String> giftCodes = Arrays.stream(giftCodesStr.split("/")).filter(giftCode -> {
                // 判断非空、本表不存在该商品、不能自己关联自己（死循环），则列为关联商品
                giftCode = giftCode.trim();
                return StrUtil.isNotBlank(giftCode) && !StrUtil.equalsAnyIgnoreCase(giftCode, templateDataVO.getUrlparam());
            }).collect(Collectors.toSet());


            LinkItemDTO linkItem = new LinkItemDTO();
            linkItem.setParentCode(templateDataVO.getUrlparam());
            linkItem.setLinkItemCodes(giftCodes);
            linkItems.add(linkItem);

        }
        dataList.add(data);
    }

    private Map<String, ProdPic> packagePicMap(List<ProdList> dataList) {
        List<String> urlparams = dataList.stream().map(ProdList::getUrlparam).collect(Collectors.toList());
        List<ProdPic> list = prodPicService.list(new LambdaQueryWrapper<ProdPic>().in(ProdPic::getItemCode, urlparams).eq(ProdPic::getDefaulted, 1));
        return list.stream().collect(Collectors.toMap(ProdPic::getItemCode, y -> y));
    }

    private void setPic(ProdListV4DTO dto, Map<String, ProdPic> picMap) {
        ProdPic prodPic = picMap.get(dto.getUrlparam());
        if (prodPic != null) {
            dto.setPicid(prodPic.getId().toString());
            dto.setPic(PicProperties.convert(prodPic));
        }
    }

    /***
     * 将导入的关联商品也要查出来放到里面
     */
    @Override
    public List<ProductVO> toProductData(List<ProdList> dataList) {
        return dataList.stream().map(x -> {
            ProdList prodList = new ProdList();
            BeanUtil.copyProperties(x, prodList);
            return dataService.getProductVO(prodList, null, dataList);
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean form(String mmCode, List<ProdList> prodListList) {
        String fileName = "textthai-" + DateUtil.now() + ".xlsx";
        String infoId = IdUtil.simpleUUID();

        ProdTemplateInfo info = new ProdTemplateInfo();
        info.setId(infoId)
                .setMmCode(mmCode)
                .setStatus(1)
                .setIsvalid(1)
                .setCreator(JwtUtils.getUsername())
                .setSheetname(fileName)
                .setDatanum((long) prodListList.size());


        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            //生成excel
            EasyExcel.write(outputStream, ProdList.class)
                    // 在 write 方法之后， 在 sheet方法之前都是设置WriteWorkbook的参数
                    .sheet("textthai")
                    .doWrite(prodListList.stream().peek(x -> {
                        x.setInfoid(infoId);
                        x.setChannelType(firstLetterName(x.getChannelType()));
                    }).collect(Collectors.toList()));

            MakroMultipartFile file = new MakroMultipartFile(fileName, fileName, ContentType.OCTET_STREAM.getValue(), outputStream.toByteArray());
            //上传文件
            String path = uploadFileToMinio(file);
            info.setFilename(path);
            importManager.importData(mmCode, info, prodListList);
            return true;
        } catch (IOException e) {
            log.error("ImportV4Service.form", e);
            return false;
        }

    }

    public static String firstLetterName(String name) {
        name = name.substring(0, 1).toUpperCase() + name.substring(1);
        return name;
    }


    @Override
    public void importData(String mmCode, ProdTemplateInfo info, List<ProdList> prodListList) {
        importManager.importData(mmCode, info, prodListList);
    }


    private void linkItemsHandle(List<ProdList> dataList, List<LinkItemDTO> linkItems, List<String> itemCodes, String infoId, String username) {
        // 赠品校验：当excel导入的商品存在赠品时，但是赠品未在excel表格中存在，则新增；若存在，则更新
        linkItems.forEach(linkItemDTO -> {
            String parentCode = linkItemDTO.getParentCode();

            //如果关联商品不存在textthai则添加一条记录
            List<String> notExistItemCodes = linkItemDTO.getLinkItemCodes().stream().filter(o -> !itemCodes.contains(o)).distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(notExistItemCodes)) {
                List<ProdStorage> storageList = storageService.list(new LambdaQueryWrapper<ProdStorage>().in(ProdStorage::getItemcode, notExistItemCodes));
                // 如果导入表中也不存在该赠品，此时将赠品加到商品中
                notExistItemCodes.forEach(itemCode -> {
                    ProdList linkItem = new ProdList();
                    ProdStorage storage = storageList.stream().filter(x -> StrUtil.equals(x.getItemcode(), itemCode)).findFirst().orElse(new ProdStorage());
                    BeanUtil.copyProperties(storage, linkItem);
                    linkItem.setId(IdUtil.simpleUUID());
                    linkItem.setPromotype(2);
                    linkItem.setUrlparam(itemCode);
                    linkItem.setCreator(username);
                    linkItem.setInfoid(infoId);
                    linkItem.setIsvalid(1);
                    linkItem.setParentCode(parentCode);
                    dataList.add(linkItem);
                });
            }

            //如果关联商品存在textthai则填充parentCode
            List<String> existItemCodes = linkItemDTO.getLinkItemCodes().stream().filter(itemCodes::contains).distinct().collect(Collectors.toList());
            if (CollUtil.isNotEmpty(existItemCodes)) {
                existItemCodes.forEach(itemCode -> {
                    ProdList item = dataList.stream().filter(product -> StrUtil.equals(product.getUrlparam(), itemCode) && ObjectUtil.equals(product.getIsvalid(), 1)).findFirst().orElse(new ProdList());
                    item.setParentCode(parentCode);
                });
            }
        });
    }


    private String uploadFileToMinio(MultipartFile file) throws IOException {
        // 上传文件到minio临时保存
        Assert.isTrue(StrUtil.isNotBlank(file.getOriginalFilename()), StatusCode.FILE_ERROR);
        String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
        StreamDTO streamDTO = new StreamDTO(file.getBytes(), suffix, file.getContentType(), "product", null);
        BaseResponse<UploadResultDTO> result = fileFeignClient.uploadInputStream(streamDTO);
        return result.getData().getPath();
    }
}
