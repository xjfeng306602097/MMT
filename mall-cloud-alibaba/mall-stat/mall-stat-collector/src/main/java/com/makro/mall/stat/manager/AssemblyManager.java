package com.makro.mall.stat.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.comparator.CompareUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.makro.mall.admin.api.MmMemberTypeFeignClient;
import com.makro.mall.admin.api.PublishFeignClient;
import com.makro.mall.admin.pojo.dto.AssemblyDataByMemberTypeDTO;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.product.api.ProdListFeignClient;
import com.makro.mall.product.pojo.entity.ProdList;
import com.makro.mall.stat.pojo.constant.CustomerTypeEnum;
import com.makro.mall.stat.pojo.snapshot.*;
import com.makro.mall.stat.service.AppUvLogService;
import com.makro.mall.stat.service.GoodsClickLogService;
import com.makro.mall.stat.service.PageViewLogService;
import com.makro.mall.template.api.TemplateFeignClient;
import com.makro.mall.template.pojo.entity.MmTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jincheng
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssemblyManager {
    private final GoodsClickLogService goodsClickLogService;
    private final PageViewLogService pageViewLogService;
    private final AppUvLogService appUvLogService;
    private final ProdListFeignClient prodListFeignClient;
    private final MmMemberTypeFeignClient mmMemberTypeFeignClient;
    private final PublishFeignClient publishFeignClient;
    private final TemplateFeignClient templateFeignClient;

    public List<PageChannelMemberType> getPageChannelMemberType(Date time) {
        List<PageChannelMemberType> all = pageViewLogService.listByTime(time);

        List<PageChannelMemberType> result = packagingPageChannelMemberType(time, all);

        //保存并将今天所有数据生成一条总数
        insertPageChannelMemberType(result);
        return result;
    }

    private List<PageChannelMemberType> packagingPageChannelMemberType(Date time, List<PageChannelMemberType> all) {
        return all.stream().filter(x -> StrUtil.isNotBlank(x.getMemberType())).map(x -> {
            PageChannelMemberType pageChannelMemberType = new PageChannelMemberType();
            pageChannelMemberType.setMmCode(x.getMmCode());
            pageChannelMemberType.setChannel(x.getChannel());
            pageChannelMemberType.setMemberType(x.getMemberType());
            String[] split = x.getMemberType().split(",");
            for (String s : split) {
                switch (CustomerTypeEnum.getNameByMemberType(s)) {
                    case "FR":
                        pageChannelMemberType.setFr(true);
                        break;
                    case "NFR":
                        pageChannelMemberType.setNfr(true);
                        break;
                    case "HO":
                        pageChannelMemberType.setHo(true);
                        break;
                    case "SV":
                        pageChannelMemberType.setSv(true);
                        break;
                    case "DT":
                        pageChannelMemberType.setDt(true);
                        break;
                    case "OT":
                        pageChannelMemberType.setOt(true);
                        break;
                    default:
                        break;
                }
            }
            pageChannelMemberType.setPv(x.getPv());
            pageChannelMemberType.setUv(x.getUv());
            pageChannelMemberType.setDate(time);

            return pageChannelMemberType;
        }).filter(ObjectUtil::isNotNull).collect(Collectors.toList());
    }

    private void insertPageChannelMemberType(List<PageChannelMemberType> result) {
        HashMap<String, PageChannelMemberType> map = new HashMap<>();
        result.forEach(x -> {
            PageChannelMemberType pageChannelMemberType = map.get(x.getMemberType());
            if (pageChannelMemberType == null) {
                pageChannelMemberType = new PageChannelMemberType();
                BeanUtil.copyProperties(x, pageChannelMemberType);
                pageChannelMemberType.setMmCode("0");
                map.put(x.getMemberType(), pageChannelMemberType);
            } else {
                pageChannelMemberType.setPv(pageChannelMemberType.getPv() + x.getPv());
                pageChannelMemberType.setUv(pageChannelMemberType.getUv() + x.getUv());
                map.put(x.getMemberType(), pageChannelMemberType);
            }
        });
        result.addAll(map.values());
    }

    public List<ChannelVisitorConversion> getChannelVisitorConversion(Date time) {
        List<ChannelVisitorConversion> list = pageViewLogService.selectTimeChannelVisitorConversion(time);
        List<ChannelVisitorConversion> result = list.stream().map(x -> {
            if (StrUtil.isEmpty(x.getChannel()) || StrUtil.isEmpty(x.getMmCode())) {
                return null;
            }
            //如果为app则取打开MM列表页UV数为总数
            Long data;
            if (StrUtil.equals(x.getChannel(), "app")) {
                data = appUvLogService.getMmPublishAppUv(time);
            } else {
                data = Long.valueOf(publishFeignClient.getMmPublishTotal(x.getMmCode(), x.getChannel()).getData());
            }
            x.setTotal(data);
            return x;
        }).filter(ObjectUtil::isNotNull).collect(Collectors.toList());
        //保存
        if (CollectionUtil.isEmpty(result)) {
            log.error("channelVisitorConversion 没有存储用户数据,有点奇怪. list:{} time:{}", list, time);
            return new ArrayList<>();
        }

        //保存并将今天所有数据生成一条总数
        List.of("email", "sms", "line", "facebook", "app").forEach(x -> {
            ChannelVisitorConversion sum = new ChannelVisitorConversion();
            sum.setMmCode("0");
            sum.setChannel(x);
            sum.setTotal(result.stream().filter(channelVisitorConversion -> ObjectUtil.equal(channelVisitorConversion.getChannel(), x)).mapToLong(ChannelVisitorConversion::getTotal).sum());
            sum.setValue(result.stream().filter(channelVisitorConversion -> ObjectUtil.equal(channelVisitorConversion.getChannel(), x)).mapToLong(ChannelVisitorConversion::getValue).sum());
            sum.setUv(result.stream().filter(channelVisitorConversion -> ObjectUtil.equal(channelVisitorConversion.getChannel(), x)).mapToLong(ChannelVisitorConversion::getUv).sum());
            result.add(sum);
        });
        return result;
    }

    private List<AssemblyDataByMemberType> splitMultipleMembers(List<AssemblyDataByMemberType> list) {
        ArrayList<AssemblyDataByMemberType> arrayList = new ArrayList<>();
        for (AssemblyDataByMemberType x : list) {
            String memberType = x.getMemberType();
            if (memberType.contains(",")) {
                String[] split = memberType.split(",");
                for (String s : split) {
                    AssemblyDataByMemberType assemblyDataByMemberType = new AssemblyDataByMemberType();
                    assemblyDataByMemberType.setMmCode(x.getMmCode());
                    assemblyDataByMemberType.setMemberType(s);
                    assemblyDataByMemberType.setUv(x.getUv());
                    assemblyDataByMemberType.setPv(x.getPv());
                    arrayList.add(assemblyDataByMemberType);
                }
            } else {
                arrayList.add(x);
            }
        }
        return arrayList;
    }

    @Nullable
    public ArrayList<AssemblyDataByMemberType> getAssemblyDataByMemberType(Date time) {
        ArrayList<AssemblyDataByMemberType> results = new ArrayList<>();
        //会员类型列表
        List<MmMemberType> memberTypeVO = mmMemberTypeFeignClient.list().getData();
        //找出昨天的mmCode memberType uv pv
        List<AssemblyDataByMemberType> list = pageViewLogService.selectTimeMemberTypeClickThroughRate(time);
        //将mmCode隔离出来作为入参
        Set<String> mmCodes = list.stream().filter(x -> ObjectUtil.isNotNull(x.getMemberType())).map(AssemblyDataByMemberType::getMmCode).collect(Collectors.toSet());
        //根据mmCode查找发送总数,再按MemberType分类
        List<AssemblyDataByMemberTypeDTO> dataDTO = publishFeignClient.getMmPublishTotalGroupByMemberType(mmCodes).getData();
        if (CollectionUtil.isEmpty(dataDTO)) {
            log.error("memberTypeClickThroughRate 没有存储用户数据,有点奇怪.mmCodes:{} time:{}", mmCodes, time);
            return null;
        }
        List<AssemblyDataByMemberType> data = dataDTO.stream().map(x -> {
            AssemblyDataByMemberType assemblyDataByMemberType = new AssemblyDataByMemberType();
            BeanUtil.copyProperties(x, assemblyDataByMemberType);
            return assemblyDataByMemberType;
        }).collect(Collectors.toList());

        //把多会员拆成单会员
        list = splitMultipleMembers(list);
        //融合list data
        List<AssemblyDataByMemberType> finalList = list;
        data.forEach(x -> {
            x.setUv(0L);
            x.setPv(0L);
            finalList.forEach(y -> {
                if (Objects.equals(x.getMmCode(), y.getMmCode()) && Objects.equals(x.getMemberType(), y.getMemberType())) {
                    x.setUv(x.getUv() + y.getUv());
                    x.setPv(x.getPv() + y.getPv());
                }
            });
            if (ObjectUtil.notEqual(x.getUv(), 0L) || ObjectUtil.notEqual(x.getTotal(), 0L)) {
                results.add(x);
            }
        });


        //保存并将今天所有数据生成一条总数
        memberTypeVO.forEach(x -> {
            AssemblyDataByMemberType sum = new AssemblyDataByMemberType();
            sum.setMmCode("0");
            sum.setMemberType(x.getId());
            sum.setTotal(results.stream().filter(result -> Objects.equals(x.getId(), result.getMemberType())).mapToLong(AssemblyDataByMemberType::getTotal).sum());
            sum.setUv(results.stream().filter(result -> Objects.equals(x.getId(), result.getMemberType())).mapToLong(AssemblyDataByMemberType::getUv).sum());
            sum.setPv(results.stream().filter(result -> Objects.equals(x.getId(), result.getMemberType())).mapToLong(AssemblyDataByMemberType::getPv).sum());
            if (ObjectUtil.notEqual(sum.getUv(), 0L) || ObjectUtil.notEqual(sum.getTotal(), 0L)) {
                results.add(sum);
            }
        });
        return results;
    }

    public List<ProductAnalysis> getProductAnalyses(Date time) {
        List<ProductAnalysis> all = goodsClickLogService.selectTimeProductAnalysis(time);
        //按MM 渠道分组
        Map<String, Map<String, List<ProductAnalysis>>> allMap = new HashMap<>(256);
        all.forEach(x -> {
            Map<String, List<ProductAnalysis>> map = allMap.containsKey(x.getMmCode()) ? allMap.get(x.getMmCode()) : new HashMap<>();
            List<ProductAnalysis> list = ObjectUtil.isNotNull(map.get(x.getChannel())) ? map.get(x.getChannel()) : new ArrayList<>();
            list.add(x);
            map.put(x.getChannel(), list);
            allMap.put(x.getMmCode(), map);
        });
        //填充0点击页面
        List<ProductAnalysis> result = new ArrayList<>();
        Map<String, List<ProdList>> map = prodListFeignClient.getMapByMmCodes(allMap.keySet()).getData();
        Map<String, MmTemplate> templateMap = templateFeignClient.getByMmCodes(ListUtil.toList(allMap.keySet())).getData();
        allMap.keySet().forEach(mmcode -> {
            List<ProdList> prodLists = map.get(mmcode);
            Map<String, List<ProductAnalysis>> ProductAnalysiss = allMap.get(mmcode);
            MmTemplate mmTemplate = templateMap.get(mmcode);
            if (ObjectUtil.isNull(prodLists) || ObjectUtil.isNull(mmTemplate)) {
                log.error("productAnalysis 获取数据为空 mmcode:{} mmTemplate:{} prodLists:{}", mmcode, mmTemplate, prodLists);
                return;
            }
            ProductAnalysiss.keySet().forEach(channel -> prodLists.forEach(prodList -> {
                ProductAnalysis no = new ProductAnalysis();
                no.setMmCode(mmcode);
                no.setNameEn(StrUtil.isNotBlank(prodList.getNameen()) ? prodList.getNameen() : "");
                no.setNameThai(StrUtil.isNotBlank(prodList.getNamethai()) ? prodList.getNamethai() : "");
                no.setGoodsCode(prodList.getUrlparam());
                no.setChannel(channel);
                no.setClicks(0L);
                no.setVisitors(0L);
                no.setPageNo(String.valueOf(prodList.getPage()));
                no.setDate(time);
                if (ObjectUtil.isNull(prodList.getPage()) || CompareUtil.compare(mmTemplate.getTemplatePageTotal(), prodList.getPage()) < 0) {
                    return;
                }

                ProductAnalysiss.get(channel).forEach(productAnalysis -> {
                    if (StrUtil.equals(productAnalysis.getGoodsCode(), prodList.getUrlparam())) {
                        no.setClicks(productAnalysis.getClicks());
                        no.setVisitors(productAnalysis.getVisitors());
                        no.setPageNo(productAnalysis.getPageNo());
                    }
                });
                result.add(no);
            }));

        });
        return result;
    }

    private void insertPagePageNoTotal(Date time, List<PagePageNo> all) {
        Set<String> pageNos = all.stream().map(PagePageNo::getPageNo).collect(Collectors.toSet());
        Set<String> channels = all.stream().map(PagePageNo::getChannel).collect(Collectors.toSet());
        channels.forEach(channel -> pageNos.forEach(x -> {
                    PagePageNo pagePageNo = new PagePageNo();
                    pagePageNo.setMmCode("0");
                    pagePageNo.setPageNo(x);
                    pagePageNo.setPv(all.stream().filter(y -> ObjectUtil.equals(x, y.getPageNo()) && ObjectUtil.equals(channel, y.getChannel())).mapToLong(PagePageNo::getPv).sum());
                    pagePageNo.setUv(all.stream().filter(y -> ObjectUtil.equals(x, y.getPageNo()) && ObjectUtil.equals(channel, y.getChannel())).mapToLong(PagePageNo::getUv).sum());
                    pagePageNo.setCount(all.stream().filter(y -> ObjectUtil.equals(x, y.getPageNo()) && ObjectUtil.equals(channel, y.getChannel())).mapToLong(PagePageNo::getCount).sum());
                    pagePageNo.setDate(time);
                    pagePageNo.setChannel(channel);
                    all.add(pagePageNo);

                }
        ));
    }

    public List<PagePageNo> getPagePageNo(Date time) {
        List<PagePageNo> all = pageViewLogService.listByPageNo(time);
        //按MM 渠道分组
        Map<String, Map<String, List<PagePageNo>>> allMap = new HashMap<>(256);
        all.forEach(x -> {
            Map<String, List<PagePageNo>> map = allMap.containsKey(x.getMmCode()) ? allMap.get(x.getMmCode()) : new HashMap<>();
            List<PagePageNo> list = ObjectUtil.isNotNull(map.get(x.getChannel())) ? map.get(x.getChannel()) : new ArrayList<>();
            list.add(x);
            map.put(x.getChannel(), list);
            allMap.put(x.getMmCode(), map);
        });
        //填充0点击页面
        List<PagePageNo> result = new ArrayList<>();
        List<String> mmcodes = all.stream().map(PagePageNo::getMmCode).distinct().collect(Collectors.toList());
        Map<String, MmTemplate> map = templateFeignClient.getByMmCodes(mmcodes).getData();
        mmcodes.forEach(mmcode -> {
            MmTemplate mmTemplate = map.get(mmcode);
            Map<String, List<PagePageNo>> pagePageNos = allMap.get(mmcode);
            if (ObjectUtil.isNull(mmTemplate)) {
                log.error("mostVisitPage 获取模板MMCODE为:{}空", mmcode);
                return;
            }
            pagePageNos.keySet().forEach(channel -> {
                for (Integer i = 1; i <= mmTemplate.getTemplatePageTotal(); i++) {
                    String pageNo = String.valueOf(i);
                    PagePageNo no = new PagePageNo();
                    no.setMmCode(mmcode);
                    no.setChannel(channel);
                    no.setPageNo(pageNo);
                    no.setCount(0L);
                    no.setPv(0L);
                    no.setUv(0L);
                    no.setDate(time);
                    pagePageNos.get(channel).forEach(pagePageNo -> {
                        if (StrUtil.equals(pagePageNo.getPageNo(), pageNo)) {
                            BeanUtil.copyProperties(pagePageNo, no);
                        }
                    });
                    result.add(no);
                }
            });

        });

        //保存并将今天所有数据生成一条总数
        insertPagePageNoTotal(time, result);
        return result;
    }

    public List<BehaviorData> getBehaviorData(Date time) {
        List<BehaviorData> behaviorData = pageViewLogService.selectTimeBehaviorData(time);
        List<BehaviorData> behaviorDataMv = pageViewLogService.selectTimeBehaviorDataMv(time);
        List<BehaviorData> behaviorDataNewUv = pageViewLogService.selectTimeBehaviorDataNewUv(time);

        insertDate(behaviorData, time);

        insertMv(behaviorData, behaviorDataMv);

        insertNewUv(behaviorData, behaviorDataNewUv);

        insertTotal(time, behaviorData);
        return behaviorData;
    }

    /**
     * 功能描述:保存并将今天所有数据生成一条总数
     *
     * @Author: 卢嘉俊
     * @Date: 2022/8/19
     */
    private void insertTotal(Date time, List<BehaviorData> behaviorData) {
        BehaviorData data = new BehaviorData();
        data.setMmCode("0");
        data.setPv(behaviorData.stream().mapToLong(BehaviorData::getPv).sum());
        data.setUv(pageViewLogService.selectTimeBehaviorDataTotalUv(time));
        data.setMv(pageViewLogService.selectTimeBehaviorDataTotalMv(time));
        data.setNewUv(pageViewLogService.selectTimeBehaviorDataTotalNewUv(time));
        data.setDate(time);
        behaviorData.add(data);
    }

    private void insertDate(List<BehaviorData> behaviorData, Date time) {
        behaviorData.forEach(x -> x.setDate(time));
    }

    private void insertMv(List<BehaviorData> behaviorData, List<BehaviorData> behaviorDataMv) {
        behaviorData.forEach(x -> {
            BehaviorData behaviorData1 = behaviorDataMv.stream().filter(mv -> Objects.equals(mv.getMmCode(), x.getMmCode())).findFirst().orElse(null);
            if (behaviorData1 == null) {
                return;
            }
            x.setMv(behaviorData1.getMv());
        });
    }

    private void insertNewUv(List<BehaviorData> behaviorData, List<BehaviorData> behaviorDataNewUv) {
        behaviorData.forEach(x -> {
            BehaviorData behaviorData1 = behaviorDataNewUv.stream().filter(mv -> Objects.equals(mv.getMmCode(), x.getMmCode())).findFirst().orElse(null);
            if (behaviorData1 == null) {
                return;
            }
            x.setNewUv(behaviorData1.getNewUv());
        });
    }
}
