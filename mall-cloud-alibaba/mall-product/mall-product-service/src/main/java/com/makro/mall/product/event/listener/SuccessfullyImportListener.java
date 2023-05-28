package com.makro.mall.product.event.listener;

import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.redis.utils.RedisUtils;
import com.makro.mall.product.event.pojo.ImportCacheEvictEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;

/**
 * @author xiaojunfeng
 * @description 导入成功事务监听
 * @date 2022/4/6
 */
@Component
public class SuccessfullyImportListener {

    @Resource
    private RedisUtils redisUtils;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void processCacheEvict(ImportCacheEvictEvent event) {
        redisUtils.del(RedisConstants.TEXT_THAI_IMPORT_PREFIX + event.getMmCode());
    }

}
