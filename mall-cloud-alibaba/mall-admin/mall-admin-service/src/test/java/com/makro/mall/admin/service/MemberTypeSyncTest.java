package com.makro.mall.admin.service;

import com.alibaba.fastjson2.JSON;
import com.makro.mall.admin.CustomSpringbootTest;
import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.common.constants.RedisConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description memberType测试
 * @date 2022/10/1
 */
@CustomSpringbootTest
public class MemberTypeSyncTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    @DisplayName("模拟同步数据到redis")
    void send() throws InterruptedException {
        String json = "[{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"1\",\"nameTh\":\"N/A\",\"nameEn\":\"N/A\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"101\",\"nameTh\":\"101 - SMALL RETAIL SHOP/STALL\",\"nameEn\":\"101 - SMALL RETAIL SHOP/STALL\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"102\",\"nameTh\":\"102 - FRUIT & VEGGIE SHOP/STALL\",\"nameEn\":\"102 - FRUIT & VEGGIE SHOP/STALL\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"103\",\"nameTh\":\"103 - BUTCHERY SHOP/STALL\",\"nameEn\":\"103 - BUTCHERY SHOP/STALL\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"104\",\"nameTh\":\"104 - DRUGS STORE\",\"nameEn\":\"104 - DRUGS STORE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"105\",\"nameTh\":\"105 - PET SHOP (FOOD/DRUGS/ACCESSORY)\",\"nameEn\":\"105 - PET SHOP (FOOD/DRUGS/ACCESSORY)\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"106\",\"nameTh\":\"106 - MINIMART\",\"nameEn\":\"106 - MINIMART\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"107\",\"nameTh\":\"107 - SUPERMARKET\",\"nameEn\":\"107 - SUPERMARKET\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"108\",\"nameTh\":\"108 - FROZEN/CHILL RETAIL SHOP\",\"nameEn\":\"108 - FROZEN/CHILL RETAIL SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"109\",\"nameTh\":\"109 - COSMETIC & BEAUTY SHOP/KIOSK\",\"nameEn\":\"109 - COSMETIC & BEAUTY SHOP/KIOSK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"110\",\"nameTh\":\"110\",\"nameEn\":\"110\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"120\",\"nameTh\":\"120 - MOVABLE VAN FOOD\",\"nameEn\":\"120 - MOVABLE VAN FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"130\",\"nameTh\":\"130 - OTHER FOOD RETAIL SHOP\",\"nameEn\":\"130 - OTHER FOOD RETAIL SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"138\",\"nameTh\":\"138\",\"nameEn\":\"138\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"140\",\"nameTh\":\"140\",\"nameEn\":\"140\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"141\",\"nameTh\":\"141 - LAO - FOOD RETAILER\",\"nameEn\":\"141 - LAO - FOOD RETAILER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"142\",\"nameTh\":\"142 - MYANMAR - FOOD RETAILER\",\"nameEn\":\"142 - MYANMAR - FOOD RETAILER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"143\",\"nameTh\":\"143 - CAMBODIA - FOOD RETAILER\",\"nameEn\":\"143 - CAMBODIA - FOOD RETAILER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"144\",\"nameTh\":\"144 - MALAYSIA - FOOD RETAILER\",\"nameEn\":\"144 - MALAYSIA - FOOD RETAILER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"16\",\"nameTh\":\"16\",\"nameEn\":\"16\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"191\",\"nameTh\":\"191\",\"nameEn\":\"191\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:01:13.000+0000\",\"updatedDate\":null,\"id\":\"193\",\"nameTh\":\"193 - ถุงเงิน: ร้านโชห่วย/มินิมาร์ท\",\"nameEn\":\"193 - Toong Ngern - Food Retailer\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:02:25.000+0000\",\"updatedDate\":null,\"id\":\"194\",\"nameTh\":\"194 - ทรูมันนี่ วอลเล็ท: ร้านโชห่วย/มินิมาร์ท\",\"nameEn\":\"194 - True Money Wallet - Food Retailer\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"195\",\"nameTh\":\"195 - ทรู สมาร์ต เมอร์ชันต์: ร้านโชห่วย/มินิมาร์ท\",\"nameEn\":\"195 - Food Retailer/Minimart True Smart Merchant\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"198\",\"nameTh\":\"198\",\"nameEn\":\"198\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"199\",\"nameTh\":\"199\",\"nameEn\":\"199\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"201\",\"nameTh\":\"201 - STATIONERY SHOP\",\"nameEn\":\"201 - STATIONERY SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"202\",\"nameTh\":\"202 - OFFICE SUPPLY SHOP\",\"nameEn\":\"202 - OFFICE SUPPLY SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"203\",\"nameTh\":\"203\",\"nameEn\":\"203\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"204\",\"nameTh\":\"204 - ELECTRONIC APPLIANCE SHOP\",\"nameEn\":\"204 - ELECTRONIC APPLIANCE SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"205\",\"nameTh\":\"205\",\"nameEn\":\"205\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"206\",\"nameTh\":\"206\",\"nameEn\":\"206\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"208\",\"nameTh\":\"208\",\"nameEn\":\"208\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"210\",\"nameTh\":\"210\",\"nameEn\":\"210\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"220\",\"nameTh\":\"220 - MOVABLE VAN NON-FOOD\",\"nameEn\":\"220 - MOVABLE VAN NON-FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"230\",\"nameTh\":\"230 - OTHER NON-FOOD SHOP\",\"nameEn\":\"230 - OTHER NON-FOOD SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"239\",\"nameTh\":\"239\",\"nameEn\":\"239\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"241\",\"nameTh\":\"241 - LAO NON-FOOD RETAILER\",\"nameEn\":\"241 - LAO NON-FOOD RETAILER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"242\",\"nameTh\":\"242 - MYANMAR - NON-FOOD RETAILER\",\"nameEn\":\"242 - MYANMAR - NON-FOOD RETAILER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"26\",\"nameTh\":\"26\",\"nameEn\":\"26\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"295\",\"nameTh\":\"ทรู สมาร์ต เมอร์ชันต์: ร้านค้าปลีกสินค้าอุปโภค\",\"nameEn\":\"295 - Non-Food Retailer True Smart Merchant\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"299\",\"nameTh\":\"299\",\"nameEn\":\"299\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"301\",\"nameTh\":\"301 - APARTMENT/DORMITORY\",\"nameEn\":\"301 - APARTMENT/DORMITORY\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"302\",\"nameTh\":\"302 - HOTEL & RESORT ( C )\",\"nameEn\":\"302 - HOTEL & RESORT ( C )\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"303\",\"nameTh\":\"303 - HOTEL & RESORT ( B )\",\"nameEn\":\"303 - HOTEL & RESORT ( B )\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"304\",\"nameTh\":\"304 - HOTEL & RESORT ( A )\",\"nameEn\":\"304 - HOTEL & RESORT ( A )\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"305\",\"nameTh\":\"305 - PUB/BAR/KARAOKE\",\"nameEn\":\"305 - PUB/BAR/KARAOKE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"306\",\"nameTh\":\"306 - THAI FOOD\",\"nameEn\":\"306 - THAI FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"307\",\"nameTh\":\"307 - THAI NORTHEASTERN FOOD\",\"nameEn\":\"307 - THAI NORTHEASTERN FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"308\",\"nameTh\":\"308 - CHINESE FOOD\",\"nameEn\":\"308 - CHINESE FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"309\",\"nameTh\":\"309 - VIETNAMESE FOOD\",\"nameEn\":\"309 - VIETNAMESE FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"310\",\"nameTh\":\"310 - JAPANESE FOOD\",\"nameEn\":\"310 - JAPANESE FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"311\",\"nameTh\":\"311 - KOREAN FOOD\",\"nameEn\":\"311 - KOREAN FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"312\",\"nameTh\":\"312 - INDIAN FOOD\",\"nameEn\":\"312 - INDIAN FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"313\",\"nameTh\":\"313 - MUSLIM/MIDDLE EAST FOOD\",\"nameEn\":\"313 - MUSLIM/MIDDLE EAST FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"314\",\"nameTh\":\"314 - ITALIAN FOOD\",\"nameEn\":\"314 - ITALIAN FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"315\",\"nameTh\":\"315 - OTHER WESTERN FOOD\",\"nameEn\":\"315 - OTHER WESTERN FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"316\",\"nameTh\":\"316 - STEAK SHOP\",\"nameEn\":\"316 - STEAK SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"317\",\"nameTh\":\"317 - PORK PAN/BARBECUE/SUKI\",\"nameEn\":\"317 - PORK PAN/BARBECUE/SUKI\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"318\",\"nameTh\":\"318 - VARIETY FOOD SHOP\",\"nameEn\":\"318 - VARIETY FOOD SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"319\",\"nameTh\":\"319 - NOODLE FOOD SHOP\",\"nameEn\":\"319 - NOODLE FOOD SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"320\",\"nameTh\":\"320 - NOODLE FOOD KIOSK\",\"nameEn\":\"320 - NOODLE FOOD KIOSK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"321\",\"nameTh\":\"321 - BAKERY SHOP\",\"nameEn\":\"321 - BAKERY SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"322\",\"nameTh\":\"322 - COFFEE/DRINKS/SNACK/BAKERY SHOP\",\"nameEn\":\"322 - COFFEE/DRINKS/SNACK/BAKERY SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"323\",\"nameTh\":\"323 - CATERING BUSINESS\",\"nameEn\":\"323 - CATERING BUSINESS\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"324\",\"nameTh\":\"324 - FAST FOOD (MEAL)\",\"nameEn\":\"324 - FAST FOOD (MEAL)\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"325\",\"nameTh\":\"325 - FAST FOOD (BAKERY & ICE CREAM)\",\"nameEn\":\"325 - FAST FOOD (BAKERY & ICE CREAM)\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"326\",\"nameTh\":\"326 - OTHER ASIAN FOOD\",\"nameEn\":\"326 - OTHER ASIAN FOOD\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"327\",\"nameTh\":\"327 - THAI SPECIAL FOOD SHOP\",\"nameEn\":\"327 - THAI SPECIAL FOOD SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"328\",\"nameTh\":\"328 - OTHER STREET FOOD SHOP\",\"nameEn\":\"328 - OTHER STREET FOOD SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"329\",\"nameTh\":\"329 - RICE FOOD KIOSK\",\"nameEn\":\"329 - RICE FOOD KIOSK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"330\",\"nameTh\":\"330 - THAI NORTHEASTERN FOOD KIOSK\",\"nameEn\":\"330 - THAI NORTHEASTERN FOOD KIOSK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"331\",\"nameTh\":\"331 - OTHER FOOD KIOSK\",\"nameEn\":\"331 - OTHER FOOD KIOSK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"332\",\"nameTh\":\"332 - COFFEE/DRINKS/SNACK KIOSK\",\"nameEn\":\"332 - COFFEE/DRINKS/SNACK KIOSK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"333\",\"nameTh\":\"333\",\"nameEn\":\"333\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"339\",\"nameTh\":\"339 - HEALTHY & ORGANIC FOOD RESTAURANT/KIOSK\",\"nameEn\":\"339 - HEALTHY & ORGANIC FOOD RESTAURANT/KIOSK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"340\",\"nameTh\":\"340\",\"nameEn\":\"340\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"341\",\"nameTh\":\"341 - LAO - HORECA\",\"nameEn\":\"341 - LAO - HORECA\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"342\",\"nameTh\":\"342 - MYANMAR - HORECA\",\"nameEn\":\"342 - MYANMAR - HORECA\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"343\",\"nameTh\":\"343 - CAMBODIA - HORECA\",\"nameEn\":\"343 - CAMBODIA - HORECA\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"344\",\"nameTh\":\"344\",\"nameEn\":\"344\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"345\",\"nameTh\":\"345\",\"nameEn\":\"345\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"346\",\"nameTh\":\"346\",\"nameEn\":\"346\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"349\",\"nameTh\":\"349\",\"nameEn\":\"349\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"350\",\"nameTh\":\"350\",\"nameEn\":\"350\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"352\",\"nameTh\":\"352\",\"nameEn\":\"352\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"354\",\"nameTh\":\"354\",\"nameEn\":\"354\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"360\",\"nameTh\":\"360\",\"nameEn\":\"360\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"373\",\"nameTh\":\"373\",\"nameEn\":\"373\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:04:12.000+0000\",\"updatedDate\":null,\"id\":\"393\",\"nameTh\":\"393 - ถุงเงิน: โรงแรม/รถเข็นแผงลอย/ร้านอาหาร/ร้านขายกาแฟ\",\"nameEn\":\"393 - Toong Ngern - Horeca\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:04:55.000+0000\",\"updatedDate\":null,\"id\":\"394\",\"nameTh\":\"394 - ทรูมันนี่ วอลเล็ท: โรงแรม/รถเข็นแผงลอย/ร้านอาหาร/ร้านขายกาแฟ\",\"nameEn\":\"394 - True Money Wallet - Horeca\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"395\",\"nameTh\":\"ทรู สมาร์ต เมอร์ชันต์: โรงแรม/รถเข็นแผงลอย/ร้านอาหาร/ร้านขายกาแฟ\",\"nameEn\":\"395 - Hotel/Food Kiosk/Restaurant/Coffee Shop True Smart Merchant\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"396\",\"nameTh\":\"396\",\"nameEn\":\"396\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"397\",\"nameTh\":\"397\",\"nameEn\":\"397\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"398\",\"nameTh\":\"398\",\"nameEn\":\"398\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"399\",\"nameTh\":\"399\",\"nameEn\":\"399\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"401\",\"nameTh\":\"401\",\"nameEn\":\"401\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"402\",\"nameTh\":\"402 - SALON & BEAUTY SHOP\",\"nameEn\":\"402 - SALON & BEAUTY SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"403\",\"nameTh\":\"403 - SPA & MASSAGE SHOP\",\"nameEn\":\"403 - SPA & MASSAGE SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"404\",\"nameTh\":\"404\",\"nameEn\":\"404\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"405\",\"nameTh\":\"405 - LAUNDRY SHOP\",\"nameEn\":\"405 - LAUNDRY SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"406\",\"nameTh\":\"406 - PHOTO & COPY SHOP\",\"nameEn\":\"406 - PHOTO & COPY SHOP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"408\",\"nameTh\":\"408 - TRAVEL AGENT\",\"nameEn\":\"408 - TRAVEL AGENT\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"409\",\"nameTh\":\"409 - PET CARE SERVICE\",\"nameEn\":\"409 - PET CARE SERVICE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"410\",\"nameTh\":\"410\",\"nameEn\":\"410\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"411\",\"nameTh\":\"411\",\"nameEn\":\"411\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"430\",\"nameTh\":\"430 - OTHER COMMERCIAL SERVICE\",\"nameEn\":\"430 - OTHER COMMERCIAL SERVICE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"439\",\"nameTh\":\"439\",\"nameEn\":\"439\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"441\",\"nameTh\":\"441\",\"nameEn\":\"441\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"450\",\"nameTh\":\"450 - CONSIGNMENT - SERVICE\",\"nameEn\":\"450 - CONSIGNMENT - SERVICE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"501\",\"nameTh\":\"501 - MEDICAL STATION (HOSPITAL/CLINIC)\",\"nameEn\":\"501 - MEDICAL STATION (HOSPITAL/CLINIC)\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"502\",\"nameTh\":\"502\",\"nameEn\":\"502\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"503\",\"nameTh\":\"503 - OFFICE (GOVERNMENT SECTOR)\",\"nameEn\":\"503 - OFFICE (GOVERNMENT SECTOR)\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"504\",\"nameTh\":\"504 - TEMPLE/MOSQUE/CHURCH\",\"nameEn\":\"504 - TEMPLE/MOSQUE/CHURCH\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"505\",\"nameTh\":\"505 - FOUNDATION/ASSOCIATION/ROTARY\",\"nameEn\":\"505 - FOUNDATION/ASSOCIATION/ROTARY\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"507\",\"nameTh\":\"507 - OFFICE (PRIVATE SECTOR)\",\"nameEn\":\"507 - OFFICE (PRIVATE SECTOR)\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"508\",\"nameTh\":\"508 - FACTORY/FARM\",\"nameEn\":\"508 - FACTORY/FARM\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"509\",\"nameTh\":\"509 - SCHOOL/COLLEGE\",\"nameEn\":\"509 - SCHOOL/COLLEGE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"510\",\"nameTh\":\"510 - UNIVERSITY\",\"nameEn\":\"510 - UNIVERSITY\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"511\",\"nameTh\":\"511 - OTHER EDUCATIONAL INSTITUTION\",\"nameEn\":\"511 - OTHER EDUCATIONAL INSTITUTION\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"512\",\"nameTh\":\"512 - CP GROUP\",\"nameEn\":\"512 - CP GROUP\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"513\",\"nameTh\":\"513\",\"nameEn\":\"513\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"522\",\"nameTh\":\"522\",\"nameEn\":\"522\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"530\",\"nameTh\":\"530 - OTHER INSTITUTION SERVICE\",\"nameEn\":\"530 - OTHER INSTITUTION SERVICE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"541\",\"nameTh\":\"541 - LAO - SERVICE\",\"nameEn\":\"541 - LAO - SERVICE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"542\",\"nameTh\":\"542\",\"nameEn\":\"542\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"543\",\"nameTh\":\"543\",\"nameEn\":\"543\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"544\",\"nameTh\":\"544 - MALAYSIA - SERVICE\",\"nameEn\":\"544 - MALAYSIA - SERVICE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"547\",\"nameTh\":\"547\",\"nameEn\":\"547\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"548\",\"nameTh\":\"548\",\"nameEn\":\"548\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"550\",\"nameTh\":\"550\",\"nameEn\":\"550\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"551\",\"nameTh\":\"551\",\"nameEn\":\"551\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"552\",\"nameTh\":\"552\",\"nameEn\":\"552\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"557\",\"nameTh\":\"557\",\"nameEn\":\"557\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:06:05.000+0000\",\"updatedDate\":null,\"id\":\"593\",\"nameTh\":\"593 - ถุงเงิน: โรงงาน/สถานศึกษา/สำนักงานต่างๆ/หน่วยงานรัฐวิสาหกิจ\",\"nameEn\":\"593 - Toong Ngern - Service\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:06:41.000+0000\",\"updatedDate\":null,\"id\":\"594\",\"nameTh\":\"594 - ทรูมันนี่ วอลเล็ท: โรงงาน/สถานศึกษา/สำนักงานต่างๆ/หน่วยงานรัฐวิสาหกิจ\",\"nameEn\":\"594 - True Money Wallet - Service\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"595\",\"nameTh\":\"ทรู สมาร์ต เมอร์ชันต์: โรงงาน/สถานศึกษา/สำนักงานต่างๆ/หน่วยงานรัฐวิสาหกิจ\",\"nameEn\":\"595 - Factory/Eductional institute/Government Sector True Smart Merchant\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"596\",\"nameTh\":\"596\",\"nameEn\":\"596\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"597\",\"nameTh\":\"597\",\"nameEn\":\"597\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"598\",\"nameTh\":\"598\",\"nameEn\":\"598\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"599\",\"nameTh\":\"599\",\"nameEn\":\"599\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"601\",\"nameTh\":\"601 - FOOD WHOLESALER\",\"nameEn\":\"601 - FOOD WHOLESALER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"602\",\"nameTh\":\"602 - FRESH FOOD WHOLESALER\",\"nameEn\":\"602 - FRESH FOOD WHOLESALER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"603\",\"nameTh\":\"603 - NON-FOOD WHOLESALER\",\"nameEn\":\"603 - NON-FOOD WHOLESALER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"604\",\"nameTh\":\"604 - FOOD & NON-FOOD WHOLESALER\",\"nameEn\":\"604 - FOOD & NON-FOOD WHOLESALER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"605\",\"nameTh\":\"605 - DRINKS & ALCOHOL WHOLESALER\",\"nameEn\":\"605 - DRINKS & ALCOHOL WHOLESALER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"606\",\"nameTh\":\"606 - EXPORTER\",\"nameEn\":\"606 - EXPORTER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"607\",\"nameTh\":\"607\",\"nameEn\":\"607\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"641\",\"nameTh\":\"641 - LAO - DISTRIBUTOR\",\"nameEn\":\"641 - LAO - DISTRIBUTOR\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"642\",\"nameTh\":\"642 - MYANMAR - DISTRIBUTOR\",\"nameEn\":\"642 - MYANMAR - DISTRIBUTOR\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"643\",\"nameTh\":\"643\",\"nameEn\":\"643\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"644\",\"nameTh\":\"644\",\"nameEn\":\"644\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"651\",\"nameTh\":\"651\",\"nameEn\":\"651\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"699\",\"nameTh\":\"699\",\"nameEn\":\"699\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"701\",\"nameTh\":\"MAKRO STAFF\",\"nameEn\":\"MAKRO STAFF\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"703\",\"nameTh\":\"CREDIT-SALES SUPPLIER\",\"nameEn\":\"CREDIT-SALES SUPPLIER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"704\",\"nameTh\":\"INTERNAL TRANSFER (WITHIN STORE)\",\"nameEn\":\"INTERNAL TRANSFER (WITHIN STORE)\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"706\",\"nameTh\":\"NON-PROFESSIONAL USER\",\"nameEn\":\"NON-PROFESSIONAL USER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"707\",\"nameTh\":\"DAYPASS - END-USER\",\"nameEn\":\"DAYPASS - END-USER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"709\",\"nameTh\":\"PREMIUM STOCK\",\"nameEn\":\"PREMIUM STOCK\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"710\",\"nameTh\":\"ลูกค้าทั่วไป\",\"nameEn\":\"INTERNET-CHANNEL END-USER\",\"active\":\"true\"},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"711\",\"nameTh\":\"Digital Marketing\",\"nameEn\":\"Digital Marketing\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"712\",\"nameTh\":\"DAYPASS - HORECA\",\"nameEn\":\"DAYPASS - HORECA\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"713\",\"nameTh\":\"DAYPASS - SERVICE\",\"nameEn\":\"DAYPASS - SERVICE\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"714\",\"nameTh\":\"DAYPASS - DISTRIBUTOR\",\"nameEn\":\"DAYPASS - DISTRIBUTOR\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"715\",\"nameTh\":\"CUSTOMER -1\",\"nameEn\":\"CUSTOMER -1\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"721\",\"nameTh\":\"โชห่วย\",\"nameEn\":\"FOOD RETAILER\",\"active\":\"true\"},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"722\",\"nameTh\":\"โรงแรม, ร้านอาหาร, จัดเลี้ยง\",\"nameEn\":\"HORECA\",\"active\":\"true\"},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"723\",\"nameTh\":\"ธุรกิจบริการ service\",\"nameEn\":\"SERVICE\",\"active\":\"true\"},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"724\",\"nameTh\":\"ผู้จัดจำหน่าย\",\"nameEn\":\"DISTRIBUTOR\",\"active\":\"true\"},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"725\",\"nameTh\":\"ร้านค้าปลีกสินค้าอุปโภค\",\"nameEn\":\"NON FOOD RETAILER\",\"active\":\"true\"},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"731\",\"nameTh\":\"DAYPASS - DONATION\",\"nameEn\":\"DAYPASS - DONATION\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"741\",\"nameTh\":\"LAO - OTHER\",\"nameEn\":\"LAO - OTHER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"742\",\"nameTh\":\"MYANMAR - OTHER\",\"nameEn\":\"MYANMAR - OTHER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"743\",\"nameTh\":\"CAMBODIA - OTHER\",\"nameEn\":\"CAMBODIA - OTHER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"744\",\"nameTh\":\"MALAYSIA - OTHER\",\"nameEn\":\"MALAYSIA - OTHER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"750\",\"nameTh\":\"CONSIGNMENT SUPPLIER\",\"nameEn\":\"CONSIGNMENT SUPPLIER\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"760\",\"nameTh\":\"760\",\"nameEn\":\"760\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:07:15.000+0000\",\"updatedDate\":null,\"id\":\"793\",\"nameTh\":\"793 - ถุงเงิน: ลูกค้าทั่วไป\",\"nameEn\":\"793 - Toong Ngern - Other\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD29422\",\"updatedBy\":null,\"createdDate\":\"2022-06-15T05:07:40.000+0000\",\"updatedDate\":null,\"id\":\"794\",\"nameTh\":\"794 - ทรูมันนี่ วอลเล็ท:ลูกค้าทั่วไป\",\"nameEn\":\"794 - True Money Wallet - Other\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"795\",\"nameTh\":\"795 - ลูกค้าที่ไม่ใช่ผู้ประกอบการ ทรู สมาร์ท เมอร์ชันต์\",\"nameEn\":\"795 - End-User True Smart Merchant\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"796\",\"nameTh\":\"796\",\"nameEn\":\"796\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"797\",\"nameTh\":\"797\",\"nameEn\":\"797\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"799\",\"nameTh\":\"FreshMakro\",\"nameEn\":\"FreshMakro\",\"active\":null},{\"createdBy\":null,\"updatedBy\":null,\"createdDate\":null,\"updatedDate\":null,\"id\":\"859\",\"nameTh\":\"INTERNAL TRANSFER (ACROSS STORES)\",\"nameEn\":\"INTERNAL TRANSFER (ACROSS STORES)\",\"active\":null},{\"createdBy\":\"nsiriwat/ZD30270\",\"updatedBy\":null,\"createdDate\":\"2022-06-16T07:40:19.000+0000\",\"updatedDate\":null,\"id\":\"999\",\"nameTh\":\"999 - ทดสอบฝ่ายการตลาด\",\"nameEn\":\"999 - MARKETING TEST\",\"active\":null}]";
        List<MmMemberType> memberTypeDTOS = JSON.parseArray(json, MmMemberType.class);
        redisTemplate.opsForList().leftPushAll(RedisConstants.MM_MEMBER_TYPE_LIST, memberTypeDTOS);
    }

    @Test
    @DisplayName("测试时区")
    void testTimezone() {
        Date now = new Date();
        System.out.println("Now is " + now.getTime());
    }


}
