package com.makro.mall.admin.sdk;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MakroProSDK {

    public JSONArray getStoreAreas() {
        String body = HttpRequest.post("https://marketplace.maknet.siammakro.cloud/user/api/v1/graphql")
                .header("Content-Type", "application/json")
                .timeout(20000)
                .body("{\n" +
                        "    \"query\": \"{\\n  getStoreAreas {\\n    s\\n    d\\n    p\\n    z\\n    se\\n    de\\n    pe\\n    status\\n    __typename\\n  }\\n}\"\n" +
                        "}")
                .execute().body();
        log.info("MakroProSDK.getStoreAreas body: {}", body);

        return JSON.parseObject(body).getJSONObject("data").getJSONArray("getStoreAreas");
    }

    public JSONArray getStoresByZipcodeSubdistrict(JSONObject x) {
       String reqBody = "{\n" +
                "    \"operationName\": \"GetStoresByZipcodeSubdistrict\",\n" +
                "    \"variables\": {\n" +
                "        \"zipcode\": \"" + x.get("z") + "\",\n" +
                "        \"subdistrict\": \"" + x.get("se") + "\"\n" +
                "    },\n" +
                "    \"query\": \"query GetStoresByZipcodeSubdistrict($zipcode: String!, $subdistrict: String!) {\\n  queryStores(zipcode: $zipcode, subdistrict: $subdistrict) {\\n    stores {\\n      name\\n      storeCode\\n      defaultStore\\n      newFlow\\n      __typename\\n    }\\n    subdistrict\\n    zipcode\\n    __typename\\n  }\\n}\"\n" +
                "}";
        String body = HttpRequest.post("https://marketplace.maknet.siammakro.cloud/marketplace/api/v1/graphql")
                .header("Content-Type", "application/json")
                .header("x-app-version", "2.2.0")
                .header("x-device-platform", "ios")
                .timeout(20000)
                .body(reqBody)
                .execute().body();
        log.info("MakroProSDK.getStoreAreas body: {}", body);
        return JSON.parseObject(body).getJSONObject("data").getJSONObject("queryStores").getJSONArray("stores");
    }
}
