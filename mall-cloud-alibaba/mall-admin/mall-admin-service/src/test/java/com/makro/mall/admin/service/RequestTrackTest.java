package com.makro.mall.admin.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.makro.mall.admin.CustomSpringbootTest;
import com.makro.mall.admin.pojo.vo.MmCustomerPageReqVO;
import com.makro.mall.admin.pojo.vo.MmCustomerVO;
import com.makro.mall.common.model.MakroPage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * @author xiaojunfeng
 * @description
 * @date 2023/4/3
 */
@CustomSpringbootTest
@Slf4j
public class RequestTrackTest {

    public static final String SEGMENT = "internal test";

    public static final Integer PAGE_SIZE = 1000;

    public static final String MM_CODE = "MM-8910306f-8830-4d54-b57a-1bccf9cec5bb";


    @Autowired
    private MmCustomerService mmCustomerService;

    @Autowired
    @Qualifier(value = "threadPoolTaskExecutor")
    private Executor threadPoolTaskExecutor;

    @Test
    @DisplayName("模拟segment=")
    void send() throws InterruptedException {
        /**
         * 120用户（2K）：
         * email:
         * 1.点击第一页，停留60s，点击23452（1-3）
         * 2.点击第二页，停留40s，点击374933（2-1）
         * sms:
         * 1.点击第一页，停留40s，点击23452（1-3）
         * 2.点击第二页，停留60s，点击374933（2-1）
         * line:
         * 1.点击第一页，停留60s，点击23452（1-3）
         * 2.点击第二页，停留40s，点击374933（2-1）
         * 220用户（2K）：
         * 1.点击第一页，停留30s，点击23452（1-3）
         * 2.点击第二页，停留10s，点击374933（2-1）
         * sms:
         * 1.点击第一页，停留10s，点击23452（1-3）
         * 2.点击第二页，停留30s，点击374933（2-1）
         * line:
         * 1.点击第一页，停留30s，点击23452（1-3）
         * 2.点击第二页，停留10s，点击374933（2-1）
         * 320用户（1K）：
         * 1.点击第一页，停留40s，点击23452（1-3）
         * 2.点击第二页，停留10s，点击374933（2-1）
         * sms:
         * 1.点击第一页，停留10s，点击23452（1-3）
         * 2.点击第二页，停留40s，点击374933（2-1）
         * line:
         * 1.点击第一页，停留40s，点击23452（1-3）
         * 2.点击第二页，停留10s，点击374933（2-1）
         */

        processTrackRequest(60000L, 40000L, "23452", "374933", "120", 152L);
        processTrackRequest(30000L, 10000L, "23452", "374933", "220", 152L);
        processTrackRequest(40000L, 10000L, "23452", "374933", "320", 152L);

    }

    private void processTrackRequest(long time1, long time2, String goodsCode1, String goodsCode2, String memberType, Long segment) {
        MmCustomerPageReqVO req = new MmCustomerPageReqVO();
        req.setSegments(Set.of(segment));
        req.setMemberTypeIds(Set.of(memberType));
        IPage<MmCustomerVO> customers = mmCustomerService.page(new MakroPage<>(1, PAGE_SIZE), req, null);
        Random rand = new Random();
        customers.getRecords().forEach(c -> {
            CompletableFuture.supplyAsync(() -> {
                String bizId = UUID.randomUUID().toString();
                String userAgent = USER_AGENTS.get(rand.nextInt(USER_AGENTS.size()));
                String ip = IP_ADDRESS.get(rand.nextInt(IP_ADDRESS.size()));
                String memberId = String.valueOf(c.getId());
                try {
                    // email
                    // page 1
                    requestPageView(memberId, "email", MM_CODE, 1, bizId, userAgent, ip);
                    requestGoodsClick(memberId, "email", MM_CODE, goodsCode1, 1, bizId, userAgent, ip);
                    requestPageStay(memberId, "email", MM_CODE, time1, 1, bizId, userAgent, ip);
                    // page 2
                    requestPageView(memberId, "email", MM_CODE, 2, bizId, userAgent, ip);
                    requestGoodsClick(memberId, "email", MM_CODE, goodsCode2, 2, bizId, userAgent, ip);
                    requestPageStay(memberId, "email", MM_CODE, time2, 2, bizId, userAgent, ip);

                    // sms
                    bizId = UUID.randomUUID().toString();
                    userAgent = USER_AGENTS.get(rand.nextInt(USER_AGENTS.size()));
                    ip = IP_ADDRESS.get(rand.nextInt(IP_ADDRESS.size()));
                    // page 1
                    requestPageView(memberId, "sms", MM_CODE, 1, bizId, userAgent, ip);
                    requestGoodsClick(memberId, "sms", MM_CODE, goodsCode1, 1, bizId, userAgent, ip);
                    requestPageStay(memberId, "sms", MM_CODE, time2, 1, bizId, userAgent, ip);
                    // page 2
                    requestPageView(memberId, "sms", MM_CODE, 2, bizId, userAgent, ip);
                    requestGoodsClick(memberId, "sms", MM_CODE, goodsCode2, 2, bizId, userAgent, ip);
                    requestPageStay(memberId, "sms", MM_CODE, time1, 2, bizId, userAgent, ip);

                    // line
                    bizId = UUID.randomUUID().toString();
                    userAgent = USER_AGENTS.get(rand.nextInt(USER_AGENTS.size()));
                    ip = IP_ADDRESS.get(rand.nextInt(IP_ADDRESS.size()));
                    // page 1
                    requestPageView(memberId, "line", MM_CODE, 1, bizId, userAgent, ip);
                    requestGoodsClick(memberId, "line", MM_CODE, goodsCode1, 1, bizId, userAgent, ip);
                    requestPageStay(memberId, "line", MM_CODE, time1, 1, bizId, userAgent, ip);
                    // page 2
                    requestPageView(memberId, "line", MM_CODE, 2, bizId, userAgent, ip);
                    requestGoodsClick(memberId, "line", MM_CODE, goodsCode2, 2, bizId, userAgent, ip);
                    requestPageStay(memberId, "line", MM_CODE, time2, 2, bizId, userAgent, ip);

                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }, threadPoolTaskExecutor).exceptionally(e -> {
                log.error("处理请求异常", e);
                return false;
            });
        });
        long totalPage = customers.getPages();
        for (int i = 2; i <= totalPage; i++) {
            customers = mmCustomerService.page(new MakroPage<>(i, PAGE_SIZE), req, null);
            customers.getRecords().forEach(c -> {
                CompletableFuture.supplyAsync(() -> {
                    String bizId = UUID.randomUUID().toString();
                    String userAgent = USER_AGENTS.get(rand.nextInt(USER_AGENTS.size()));
                    String ip = IP_ADDRESS.get(rand.nextInt(IP_ADDRESS.size()));
                    String memberId = String.valueOf(c.getId());
                    try {
                        // email
                        // page 1
                        requestPageView(memberId, "email", MM_CODE, 1, bizId, userAgent, ip);
                        requestGoodsClick(memberId, "email", MM_CODE, goodsCode1, 1, bizId, userAgent, ip);
                        requestPageStay(memberId, "email", MM_CODE, time1, 1, bizId, userAgent, ip);
                        // page 2
                        requestPageView(memberId, "email", MM_CODE, 2, bizId, userAgent, ip);
                        requestGoodsClick(memberId, "email", MM_CODE, goodsCode2, 2, bizId, userAgent, ip);
                        requestPageStay(memberId, "email", MM_CODE, time2, 2, bizId, userAgent, ip);

                        // sms
                        bizId = UUID.randomUUID().toString();
                        userAgent = USER_AGENTS.get(rand.nextInt(USER_AGENTS.size()));
                        ip = IP_ADDRESS.get(rand.nextInt(IP_ADDRESS.size()));
                        // page 1
                        requestPageView(memberId, "sms", MM_CODE, 1, bizId, userAgent, ip);
                        requestGoodsClick(memberId, "sms", MM_CODE, goodsCode1, 1, bizId, userAgent, ip);
                        requestPageStay(memberId, "sms", MM_CODE, time2, 1, bizId, userAgent, ip);
                        // page 2
                        requestPageView(memberId, "sms", MM_CODE, 2, bizId, userAgent, ip);
                        requestGoodsClick(memberId, "sms", MM_CODE, goodsCode2, 2, bizId, userAgent, ip);
                        requestPageStay(memberId, "sms", MM_CODE, time1, 2, bizId, userAgent, ip);

                        // line
                        bizId = UUID.randomUUID().toString();
                        userAgent = USER_AGENTS.get(rand.nextInt(USER_AGENTS.size()));
                        ip = IP_ADDRESS.get(rand.nextInt(IP_ADDRESS.size()));
                        // page 1
                        requestPageView(memberId, "line", MM_CODE, 1, bizId, userAgent, ip);
                        requestGoodsClick(memberId, "line", MM_CODE, goodsCode1, 1, bizId, userAgent, ip);
                        requestPageStay(memberId, "line", MM_CODE, time1, 1, bizId, userAgent, ip);
                        // page 2
                        requestPageView(memberId, "line", MM_CODE, 2, bizId, userAgent, ip);
                        requestGoodsClick(memberId, "line", MM_CODE, goodsCode2, 2, bizId, userAgent, ip);
                        requestPageStay(memberId, "line", MM_CODE, time2, 2, bizId, userAgent, ip);

                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }, threadPoolTaskExecutor).exceptionally(e -> {
                    log.error("处理请求异常", e);
                    return false;
                });
            });
        }
    }


    private void requestPageView(String memberId, String channel, String mmCode, Integer pageNo, String bizId, String userAgent, String ip) throws IOException, InterruptedException {
        Random rand = new Random();
        HttpURLConnection connection = (HttpURLConnection) new URL("https://dev-mail-api.makrogo.com/makro-stat/api/v1/track/pv").openConnection();
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
    }

    private void requestGoodsClick(String memberId, String channel, String mmCode, String goodsCode, Integer pageNo, String bizId, String userAgent, String ip) throws IOException, InterruptedException {
        Random rand = new Random();
        HttpURLConnection connection = (HttpURLConnection) new URL("https://dev-mail-api.makrogo.com/makro-stat/api/v1/track/goods/click").openConnection();
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
    }

    private void requestPageStay(String memberId, String channel, String mmCode, Long stayTime, Integer pageNo, String bizId, String userAgent, String ip) throws IOException, InterruptedException {
        Random rand = new Random();
        HttpURLConnection connection = (HttpURLConnection) new URL("https://dev-mail-api.makrogo.com/makro-stat/api/v1/track/stay").openConnection();
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
    }

    public static final List<String> USER_AGENTS = List.of("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:54.0) Gecko/20100101 Firefox/54.0",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:53.0) Gecko/20100101 Firefox/53.0",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:53.0) Gecko/20100101 Firefox/53.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; AS; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:52.0) Gecko/20100101 Firefox/52.0",
            "Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; MDDRJS; rv:11.0) like Gecko",
            "Mozilla/5.0 (Windows NT 6.1; Win64; x64; Trident/7.0; MDDRJS; rv:11.0) like Gecko",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.2 Safari/605.1.15",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 YaBrowser/21.9.0.1044 Yowser/2.5 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 YaBrowser/21.9.0.1044 Yowser/2.5 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.22",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/94.0.4606.78 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.0 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) GSA/161.0.390576739 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) FxiOS/35.0 Mobile/15E148 Safari/605.1.15",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/94.0.4606.71 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/94.0.4606.61 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/93.0.4577.78 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/93.0.4577.63 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.82 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.22 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.78 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 YaBrowser/21.9.0.1044.00 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.71 YaBrowser/21.9.0.1044.00 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 11; SM-G975F) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.61 YaBrowser/21.9.0.1044.00 Mobile Safari/537.36");


    public static final List<String> IP_ADDRESS = List.of("23.456.789.012",
            "11.222.333.444",
            "5.66.77.88",
            "92.168.1.1",
            "0.0.0.1",
            "23.45.67.89",
            "8.76.54.32",
            "7.65.43.21",
            "4.32.10.98",
            "2.34.56.78",
            "3.45.67.89",
            "4.56.78.90",
            "5.67.89.01",
            "6.78.90.12",
            "7.89.01.23",
            "8.90.12.34",
            "9.01.23.45",
            "0.12.34.56",
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
