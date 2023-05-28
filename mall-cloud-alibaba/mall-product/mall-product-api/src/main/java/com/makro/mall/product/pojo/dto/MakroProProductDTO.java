package com.makro.mall.product.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/3/24
 */
@NoArgsConstructor
@Data
public class MakroProProductDTO {

    @JsonProperty("barcode")
    private String barcode;
    @JsonProperty("brand")
    private String brand;
    @JsonProperty("brandEn")
    private String brandEn;
    @JsonProperty("categories")
    private List<String> categories;
    @JsonProperty("categoryIds")
    private List<String> categoryIds;
    @JsonProperty("collectionIds")
    private List<String> collectionIds;
    @JsonProperty("collections")
    private List<String> collections;
    @JsonProperty("displayPrice")
    private Integer displayPrice;
    @JsonProperty("filterStoreCode")
    private List<String> filterStoreCode;
    @JsonProperty("hasPromotions")
    private Integer hasPromotions;
    @JsonProperty("id")
    private String id;
    @JsonProperty("images")
    private List<String> images;
    @JsonProperty("inStock")
    private Integer inStock;
    @JsonProperty("inventoryQuantity")
    private Integer inventoryQuantity;
    @JsonProperty("isDropShipment")
    private Boolean isDropShipment;
    @JsonProperty("keywords")
    private List<String> keywords;
    @JsonProperty("makroId")
    private String makroId;
    @JsonProperty("originalPrice")
    private Integer originalPrice;
    @JsonProperty("priceUnit")
    private String priceUnit;
    @JsonProperty("primaryCategoryId")
    private String primaryCategoryId;
    @JsonProperty("productId")
    private String productId;
    @JsonProperty("publishedAt")
    private Long publishedAt;
    @JsonProperty("seller")
    private String seller;
    @JsonProperty("shortDescription")
    private String shortDescription;
    @JsonProperty("sku")
    private String sku;
    @JsonProperty("soldCount")
    private Integer soldCount;
    @JsonProperty("status")
    private String status;
    @JsonProperty("title")
    private String title;
    @JsonProperty("titleEn")
    private String titleEn;
    @JsonProperty("unitSize")
    private String unitSize;
}
