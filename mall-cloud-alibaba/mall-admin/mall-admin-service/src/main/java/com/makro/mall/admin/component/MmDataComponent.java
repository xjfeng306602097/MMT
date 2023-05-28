package com.makro.mall.admin.component;

import com.makro.mall.admin.pojo.entity.MmMemberType;
import com.makro.mall.admin.pojo.vo.MmDataAuthVO;
import com.makro.mall.admin.pojo.vo.MmStoreVO;
import com.makro.mall.common.model.Assert;
import com.makro.mall.common.model.StatusCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xiaojunfeng
 * @description
 * @date 2022/3/2
 */
//@Component
public class MmDataComponent {

    @Resource
    private RestTemplate restTemplate;

    @Value("${mm.data.token.url}")
    private String tokenUrl;

    @Value("${mm.data.token.authorization}")
    private String authorization;

    @Value("${mm.data.token.client_id}")
    private String clientId;

    @Value("${mm.data.token.client_secret}")
    private String clientSecret;

    @Value("${mm.data.token.scope}")
    private String scope;

    @Value("${mm.data.token.grant_type}")
    private String grantType;

    @Value("${mm.data.token.username}")
    private String username;

    @Value("${mm.data.token.password}")
    private String password;

    @Value("${mm.data.member-type.url}")
    private String memberTypeUrl;

    @Value("${mm.data.store.url}")
    private String storeUrl;

    public String token() {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setBasicAuth(authorization);
        // 表单提交
        requestHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 封装参数，千万不要替换为Map与HashMap，否则参数无法传递
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.set("client_id", clientId);
        params.set("client_secret", clientSecret);
        params.set("scope", scope);
        params.set("grant_type", grantType);
        params.set("username", username);
        params.set("password", password);
        HttpEntity requestEntity = new HttpEntity(params, requestHeaders);
        MmDataAuthVO authVO = restTemplate.postForObject(tokenUrl, requestEntity, MmDataAuthVO.class);
        Assert.isTrue(authVO != null, StatusCode.MM_DATA_AUTH_FAILED);
        return authVO.getAccess_token();
    }

    public List<MmMemberType> memberTypes() {
        String token = token();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", token);
        ResponseEntity<List<MmMemberType>> entity = restTemplate.exchange(memberTypeUrl,
                HttpMethod.GET, new HttpEntity<>(requestHeaders),
                new ParameterizedTypeReference<List<MmMemberType>>() {
                });
        return entity.getBody();
    }

    public List<MmStoreVO> stores() {
        String token = "Bearer " + token();
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Authorization", token);
        ResponseEntity<List<MmStoreVO>> entity = restTemplate.exchange(storeUrl,
                HttpMethod.GET, new HttpEntity<>(requestHeaders),
                new ParameterizedTypeReference<List<MmStoreVO>>() {
                });
        return entity.getBody();
    }

}
