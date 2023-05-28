package com.makro.mall.job.processor.admin;

import com.makro.mall.admin.api.MmStoreFeignClient;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StoresProcessor {
    private final MmStoreFeignClient mmStoreFeignClient;
    @XxlJob("syncFromMakro")
    public Boolean syncFromMakro() {
        return mmStoreFeignClient.syncFromMakro().getData();
    }

}
