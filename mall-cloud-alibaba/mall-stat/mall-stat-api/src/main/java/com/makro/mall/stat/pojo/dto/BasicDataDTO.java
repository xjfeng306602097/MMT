package com.makro.mall.stat.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author jincheng
 */
@Data
@NoArgsConstructor
public class BasicDataDTO {

    private final List<String> enums = BasicDataEnum.listAll();

    private final Map<String, BasicData> summary = new HashMap<String, BasicData>();

    public BasicDataDTO(Map<String, BasicData> result) {
        super();
        this.summary.putAll(result);
    }

    public static class Builder {

        private final Map<String, BasicData> summary = new HashMap<String, BasicData>();

        public Builder generate(String key, BasicData data) {
            this.summary.put(key, data);
            return this;
        }

        public BasicDataDTO build() {
            return new BasicDataDTO(this.summary);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public static enum BasicDataEnum {
        /**
         * 筛选枚举
         */
        LATEST("latest", "今天"),
        YESTERDAY("yesterday", "昨天"),
        WEEK("week", "上周"),
        LAST_14_DAYS("last_14_days", "前14天");

        @Getter
        private final String code;

        @Getter
        private final String description;

        BasicDataEnum(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public static List<String> listAll() {
            return Stream.of(values()).map(BasicDataEnum::getCode).collect(Collectors.toList());
        }

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BasicData {
        @ApiModelProperty("当天计数")
        private long count;
        @ApiModelProperty("环比差值")
        private long chain;
        @ApiModelProperty("环比差值")
        private BigDecimal chainRatio;
    }
}
