package com.makro.mall.admin.service;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.makro.mall.admin.CustomSpringbootTest;
import com.makro.mall.common.util.AesBase62Util;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/4/3
 */
@CustomSpringbootTest
@Slf4j
public class RequestTrackTest2 {

    public static final String MM_CODE = "MM-b3cf8c10-2a74-43de-9fc9-2e8963f57270";


    /**
     * 每日点击
     * 发送6名用户,抽四个进行点击浏览停留
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> memberIds = List.of("125574", "125573", "125572", "125571", "125570", "125569");
        List<Integer> pages = List.of(1, 2);
        List<String> itemCode1 = List.of("0", "226977", "206781");
        List<String> itemCode2 = List.of("0", "187898", "123155");
        List<String> channels = List.of("email", "sms", "line");
        List<String> randomEleList = RandomUtil.randomEleList(memberIds, 2);
        for (String random : randomEleList) {
            log.info(random);
            String memberId = AesBase62Util.encode(Long.valueOf(random));
            //随机渠道
            String channel = RandomUtil.randomEle(channels);
            String bizId = UUID.randomUUID().toString();
            String userAgent = RandomUtil.randomEle(USER_AGENTS);
            String ip = RandomUtil.randomEle(IP_ADDRESS);
            //遍历页面
            for (Integer page : pages) {
                //发送pv,stay
                requestPageView(memberId, channel, MM_CODE, page, bizId, userAgent, ip);
                requestPageStay(memberId, channel, MM_CODE, RandomUtil.randomLong(10000, 50000), page, bizId, userAgent, ip);
                //随机点击商品,0下一页
                int randomInt = RandomUtil.randomInt(1, 3);
                for (int i = 0; i < randomInt; i++) {
                    String itemCode = page == 1 ? RandomUtil.randomEle(itemCode1) : RandomUtil.randomEle(itemCode2);
                    if (Objects.equals(itemCode, "0")) {
                        break;
                    }
                    requestGoodsClick(memberId, channel, MM_CODE, itemCode, page, bizId, userAgent, ip);
                }
            }
        }
    }


    private static void requestPageView(String memberId, String channel, String mmCode, Integer pageNo, String bizId, String userAgent, String ip) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://uat-gateway-api.makromail.atmakro.com/makro-stat/api/v1/track/pv").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("X-Forwarded-For", ip);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        JSONObject json = new JSONObject();
        json.put("channel", channel);
        json.put("memberNo", memberId);
        json.put("mmCode", mmCode);
        json.put("pageNo", pageNo);
        json.put("pageUrl", "https://dev-file.makrogo.com/makro-htmls/mm/MM-c55f009e-e6c4-43c3-8082-378ee2d63feb.html");
        json.put("storeCode", "999");
        json.put("bizId", bizId);
        os.write(json.toJSONString().getBytes());
        os.flush();
        os.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            log.error("requestPageView error, responseCode={}", responseCode);
        }
        log.info("requestPageView channel:{},memberId:{},pageNo:{}",channel,memberId,pageNo);
    }

    private static void requestGoodsClick(String memberId, String channel, String mmCode, String goodsCode, Integer pageNo, String bizId, String userAgent, String ip) throws IOException, InterruptedException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://uat-gateway-api.makromail.atmakro.com/makro-stat/api/v1/track/goods/click").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("X-Forwarded-For", ip);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        JSONObject json = new JSONObject();
        json.put("channel", channel);
        json.put("memberNo", memberId);
        json.put("mmCode", mmCode);
        json.put("pageNo", pageNo);
        json.put("goodsCode", goodsCode);
        json.put("storeCode", "999");
        json.put("bizId", bizId);
        os.write(json.toJSONString().getBytes());
        os.flush();
        os.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            log.error("requestPageView error, responseCode={}", responseCode);
        }
        log.info("requestGoodsClick channel:{},memberId:{},pageNo:{},goodsCode:{}",channel,memberId,pageNo,goodsCode);
    }

    private static void requestPageStay(String memberId, String channel, String mmCode, Long stayTime, Integer pageNo, String bizId, String userAgent, String ip) throws IOException, InterruptedException {
        HttpURLConnection connection = (HttpURLConnection) new URL("https://uat-gateway-api.makromail.atmakro.com/makro-stat/api/v1/track/stay").openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("X-Forwarded-For", ip);
        connection.setDoOutput(true);
        OutputStream os = connection.getOutputStream();
        JSONObject json = new JSONObject();
        json.put("channel", channel);
        json.put("memberNo", memberId);
        json.put("mmCode", mmCode);
        json.put("pageNo", pageNo);
        json.put("stayTime", stayTime);
        json.put("storeCode", "999");
        json.put("bizId", bizId);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(json);
        os.write(jsonArray.toJSONString().getBytes());
        os.flush();
        os.close();
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            log.error("requestPageView error, responseCode={}", responseCode);
        }
        log.info("requestPageStay channel:{},memberId:{},pageNo:{},goodsCode:{}",channel,memberId,pageNo,stayTime);
    }

    public static final List<String> USER_AGENTS = List.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/94.0.4606.78 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 YaBrowser/21.9.0.1044.00 Mobile Safari/537.36");


    public static final List<String> IP_ADDRESS = List.of(
            "5.66.77.88",
            "01.102.103.104",
            "05.106.107.108",
            "09.110.111.112",
            "13.114.115.116",
            "17.118.119.120",
            "21.122.123.124",
            "25.126.127.128",
            "29.130.131.132",
            "33.134.135.136",
            "37.138.139.140",
            "41.142.143.144",
            "45.146.147.148",
            "49.150.151.152",
            "53.154.155.156",
            "57.158.159.160",
            "61.162.163.164",
            "65.166.167.168",
            "69.170.171.172",
            "73.174.175.176",
            "77.178.179.180",
            "81.182.183.184",
            "85.186.187.188",
            "89.190.191.192",
            "93.194.195.196",
            "97.198.199.200",
            "01.202.203.204",
            "05.206.207.208",
            "09.210.211.212",
            "13.214.215.216",
            "17.218.219.220",
            "21.222.223.224",
            "25.226.227.228",
            "29.230.231.232");

}
