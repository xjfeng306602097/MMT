package com.makro.mall.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.makro.mall.admin.mapper.MmStoreMapper;
import com.makro.mall.admin.pojo.entity.MmStore;
import com.makro.mall.admin.sdk.MakroProSDK;
import com.makro.mall.admin.service.MmStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Service
@Slf4j
public class MmStoreServiceImpl extends ServiceImpl<MmStoreMapper, MmStore>
        implements MmStoreService {

    @Resource
    private MakroProSDK makroProSDK;

    @Override
    //@Cacheable(cacheNames = "system", key = "'storeList:'+#status+':'+#name+':'+#isDeleted")
    public List<MmStore> getList(String code, Integer status, String name, Integer isDeleted) {
        LambdaQueryWrapper<MmStore> queryWrapper = new LambdaQueryWrapper<MmStore>()
                .eq(StrUtil.isNotBlank(code), MmStore::getCode, code)
                .like(StrUtil.isNotBlank(name), MmStore::getName, name)
                .eq(status != null, MmStore::getStatus, status)
                .eq(isDeleted != null, MmStore::getDeleted, isDeleted)
                .orderByAsc(MmStore::getSort);
        return this.list(queryWrapper);
    }


    @Override
    public void syncFromMakro() {
        log.info("正在执行MmStoreService.syncFromMakro");
        JSONArray storeAreas = makroProSDK.getStoreAreas();

        //将传入数组拆分
        int splitNum = 10;
        List<List<Object>> split = Stream.iterate(0, n -> n + 1)
                .limit((storeAreas.size() + splitNum - 1) / splitNum)
                .parallel()
                .map(a -> storeAreas.parallelStream().skip((long) a * splitNum).limit(splitNum).collect(Collectors.toList()))
                .filter(b -> !b.isEmpty())
                .collect(Collectors.toList());

        Set<Object> storeSet = new ConcurrentHashSet<>(120);
        CompletableFuture<Object>[] futures = new CompletableFuture[split.size()];
        for (int i = 0; i < split.size(); i++) {
            List<Object> x = split.get(i);
            futures[i] = CompletableFuture.supplyAsync(() -> {
                        x.forEach(y -> {
                            JSONArray stores = makroProSDK.getStoresByZipcodeSubdistrict((JSONObject) y);
                            storeSet.addAll(stores);
                        });
                        return null;
                    })
                    .exceptionally(e -> {
                        log.error("MmStoreService.syncFromMakro", e);
                        return null;
                    });
        }

        CompletableFuture.allOf(futures).join();

        //存MMSTORE
        Set<MmStore> mmStores = storeSet.stream().map(x -> {
            JSONObject x1 = (JSONObject) x;
            MmStore mmStore = new MmStore();
            mmStore.setCode(x1.getString("storeCode"));
            mmStore.setName(x1.getString("name"));
            return mmStore;
        }).collect(Collectors.toSet());

        List<MmStore> stores = list(new LambdaQueryWrapper<MmStore>().in(MmStore::getCode, mmStores.stream().map(MmStore::getCode).collect(Collectors.toList())));
        List<String> existList = stores.stream().map(MmStore::getCode).collect(Collectors.toList());
        // 过滤已存在的store
        List<MmStore> addList = mmStores.stream().filter(x -> !existList.contains(x.getCode())).map(s -> {
            MmStore store = new MmStore();
            store.setCode(s.getCode());
            store.setName(s.getName());
            return store;
        }).collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(addList)) {
            addList.forEach(this::save);
        }
    }

}




