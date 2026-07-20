package com.daily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import com.daily.service.AuthService;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "微信登录", description = "微信小程序登录认证接口")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "微信登录", description = "用 wx.login() 返回的 code 换 JWT token")
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        if (code == null || code.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("code", 1);
            err.put("msg", "缺少 code 参数");
            return err;
        }
        try {
            Map<String, Object> result = authService.login(code);
            Map<String, Object> ok = new HashMap<>();
            ok.put("code", 0);
            ok.put("data", result);
            return ok;
        } catch (Exception e) {
            Map<String, Object> err = new HashMap<>();
            err.put("code", 1);
            err.put("msg", e.getMessage());
            return err;
        }
    }
}
