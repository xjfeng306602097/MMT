package com.makro.mall.admin.common.constant;

/**
 * @Author: 卢嘉俊
 * @Date: 2022/9/2 line模板
 */
public interface LineConstants {
    String HTML_MM_PUBLISH_JOB_LINE_TEMPLATE1 = "{\n" +
            "    \"to\":${to},\n" +
            "    \"messages\":[\n" +
            "        {\n" +
            "            \"type\":\"flex\",\n" +
            "            \"altText\":\"${subject}\",\n" +
            "            \"contents\":{\n" +
            "                \"type\":\"carousel\",\n" +
            "                \"contents\":[\n" +
            "                    {\n" +
            "                        \"type\":\"bubble\",\n" +
            "                        \"size\":\"giga\",\n" +
            "                        \"body\":{\n" +
            "                            \"type\":\"box\",\n" +
            "                            \"layout\":\"vertical\",\n" +
            "                            \"contents\":[\n" +
            "                                {\n" +
            "                                    \"type\":\"image\",\n" +
            "                                    \"url\":\"${img}\",\n" +
            "                                    \"size\":\"full\",\n" +
            "                                    \"aspectRatio\":\"${w}:${h}\",\n" +
            "                                    \"aspectMode\":\"cover\",\n" +
            "                                    \"action\": {\n" +
            "                                      \"type\": \"uri\",\n" +
            "                                      \"label\": \"action\",\n" +
            "                                      \"uri\":\"${url}?liffid=${liffid}&mm=${mmCode}&c=&p=${pageNo}&s=${storeCode}&ts=${time}\"\n" +
            "                                    }" +
            "                                }\n" +
            "                            ],\n" +
            "                            \"paddingAll\":\"0px\"\n" +
            "                        },\n" +
            "                        \"footer\":{\n" +
            "                            \"type\":\"box\",\n" +
            "                            \"layout\":\"vertical\",\n" +
            "                            \"contents\":[\n" +
            "                                {\n" +
            "                                    \"type\":\"button\",\n" +
            "                                    \"style\":\"primary\",\n" +
            "                                    \"action\":{\n" +
            "                                        \"type\":\"uri\",\n" +
            "                                        \"label\":\"คลิกเพื่อดูรายละเอียด\",\n" +
            "                                        \"uri\":\"${url}?liffid=${liffid}&mm=${mmCode}&c=&p=${pageNo}&s=${storeCode}&ts=${time}\"\n" +
            "                                    },\n" +
            "                                    \"color\":\"#ff0000\"\n" +
            "                                }\n" +
            "                            ],\n" +
            "                            \"flex\":1\n" +
            "                        },\n" +
            "                        \"styles\":{\n" +
            "                            \"footer\":{\n" +
            "                                \"separator\":false\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    String HTML_MM_PUBLISH_JOB_LINE_TEMPLATE2 = "{\n" +
            "    \"to\":${to},\n" +
            "    \"messages\":[\n" +
            "        {\n" +
            "            \"type\":\"flex\",\n" +
            "            \"altText\":\"${subject}\",\n" +
            "            \"contents\":{\n" +
            "  \"type\": \"carousel\",\n" +
            "  \"contents\": [\n" +
            "    {\n" +
            "      \"type\": \"bubble\",\n" +
            "      \"size\": \"giga\",\n" +
            "      \"hero\": {\n" +
            "        \"type\": \"box\",\n" +
            "        \"layout\": \"vertical\",\n" +
            "        \"contents\": [\n" +
            "          {\n" +
            "            \"type\": \"image\",\n" +
            "           \"url\":\"${img}\",\n" +
            "            \"size\": \"full\",\n" +
            "            \"aspectRatio\": \"${w}:${h}\",\n" +
            "            \"aspectMode\": \"cover\",\n" +
            "            \"action\": {\n" +
            "              \"type\": \"uri\",\n" +
            "              \"label\": \"action\",\n" +
            "              \"uri\":\"${url}?liffid=${liffid}&mm=${mmCode}&c=&p=${pageNo}&s=${storeCode}&ts=${time}\"\n" +
            "            }" +
            "          }\n" +
            "        ],\n" +
            "        \"paddingAll\": \"0px\"\n" +
            "      },\n" +
            "      \"body\": {\n" +
            "        \"type\": \"box\",\n" +
            "        \"layout\": \"vertical\",\n" +
            "        \"contents\": [\n" +
            "          {\n" +
            "            \"type\": \"text\",\n" +
            "            \"text\":\"${subject}\",\n" +
            "            \"size\": \"sm\",\n" +
            "            \"wrap\": true\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"footer\": {\n" +
            "        \"type\": \"box\",\n" +
            "        \"layout\": \"vertical\",\n" +
            "        \"contents\": [\n" +
            "          {\n" +
            "            \"type\": \"button\",\n" +
            "            \"style\": \"primary\",\n" +
            "            \"action\": {\n" +
            "              \"type\": \"uri\",\n" +
            "              \"label\": \"คลิกเพื่อดูรายละเอียด\",\n" +
            "              \"uri\":\"${url}?liffid=${liffid}&mm=${mmCode}&c=&p=${pageNo}&s=${storeCode}&ts=${time}\"\n" +
            "            },\n" +
            "            \"color\": \"#ff0000\"\n" +
            "          }\n" +
            "        ],\n" +
            "        \"flex\": 1\n" +
            "      },\n" +
            "      \"styles\": {\n" +
            "        \"footer\": {\n" +
            "          \"separator\": false\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  ]\n" +
            "}\n" +
            "        }\n" +
            "    ]\n" +
            "}";

    String liff_URL = "https://liff.line.me/";

}
