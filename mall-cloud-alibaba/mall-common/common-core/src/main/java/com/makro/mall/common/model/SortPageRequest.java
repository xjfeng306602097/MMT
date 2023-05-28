package com.makro.mall.common.model;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.makro.mall.common.constants.RedisConstants;
import com.makro.mall.common.util.MakroReflectionUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2021/11/15
 */
@Data
public class SortPageRequest<T> extends BasePageRequest implements RedisConstants {

    @ApiModelProperty(value = "请求体", required = true)
    private T req;
    @ApiModelProperty(hidden = true)
    private String cachePrefix;

    private List<SortItem> sortItems = new ArrayList<>();

    public static String humpToLine(String str) {
        return str.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    public String getSortSql() {
        return getSortSql("");
    }

    public String getSortSql(String s) {
        QueryWrapper queryWrapper = new QueryWrapper();
        for (SortItem sortItem : sortItems) {
            if (sortItem.asc) {
                queryWrapper.orderByAsc(humpToLine(s + sortItem.getColumn()));
            } else {
                queryWrapper.orderByDesc(humpToLine(s + sortItem.getColumn()));
            }
        }
        return queryWrapper.getTargetSql();
    }

    public String generateRedisKey(String... fieldNames) {
        StringBuilder buffer = new StringBuilder();
        buffer.append(StrUtil.isEmpty(cachePrefix) ? DEFAULT_PREFIX : cachePrefix);
        if (fieldNames.length != 0 && req != null) {
            for (String fieldName : fieldNames) {
                if (StrUtil.isEmpty(fieldName)) {
                    continue;
                }
                String value = MakroReflectionUtils.getFieldValue(req, fieldName, "").toString();
                if (StrUtil.isEmpty(value)) {
                    continue;
                }
                buffer.append(fieldName)
                        .append(DELIMITER)
                        .append(value)
                        .append(DELIMITER);
            }
        }
        for (SortItem sortItem : sortItems) {
            buffer.append(sortItem.getColumn()).append(DELIMITER).append(sortItem.asc).append(DELIMITER);
        }
        buffer.append("page").append(DELIMITER).append(getPage()).append(DELIMITER).append("limit").append(DELIMITER).append(getLimit());
        return buffer.substring(0, buffer.length() - 1);
    }

    @Data
    public static class SortItem {
        private String column;

        private boolean asc;
    }


}
