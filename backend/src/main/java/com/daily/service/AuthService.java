package com.daily.service;

import com.daily.config.JwtUtil;
import com.daily.entity.Template;
import com.daily.entity.User;
import com.daily.mapper.TemplateMapper;
import com.daily.mapper.UserMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final TemplateMapper templateMapper;
    private final JwtUtil jwtUtil;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    private final String appId;
    private final String appSecret;

    public AuthService(UserMapper userMapper,
                       TemplateMapper templateMapper,
                       JwtUtil jwtUtil,
                       RestTemplate restTemplate,
                       ObjectMapper objectMapper,
                       @Value("${wechat.app-id}") String appId,
                       @Value("${wechat.app-secret}") String appSecret) {
        this.userMapper = userMapper;
        this.templateMapper = templateMapper;
        this.jwtUtil = jwtUtil;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public Map<String, Object> login(String code) throws Exception {
        // 1. 调微信接口换 openid
        String url = String.format(
                "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                appId, appSecret, URLEncoder.encode(code, StandardCharsets.UTF_8));
        String resp = restTemplate.getForObject(url, String.class);
        JsonNode json = objectMapper.readTree(resp);

        if (json.has("errcode") && json.get("errcode").asInt() != 0) {
            String errmsg = json.has("errmsg") ? json.get("errmsg").asText() : "unknown";
            throw new RuntimeException("WeChat login failed: " + errmsg);
        }

        String openid = json.get("openid").asText();

        // 2. 查数据库，不存在则创建
        User user = userMapper.findByOpenid(openid);
        boolean isNew = false;
        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            userMapper.insert(user);
            isNew = true;

            // 新用户自动创建默认模版
            Template defaultTpl = new Template();
            defaultTpl.setUserId(user.getId());
            defaultTpl.setName("默认模版");
            defaultTpl.setContent("【业绩日报】{date}\n{销售额}\n{客户数}\n{备注}");
            defaultTpl.setIsDefault(1);
            templateMapper.insert(defaultTpl);
        }

        // 3. 生成 JWT
        String token = jwtUtil.generateToken(user.getId());

        return Map.of(
                "token", token,
                "userId", user.getId().toString(),
                "isNew", isNew
        );
    }
}
