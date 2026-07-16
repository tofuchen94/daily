package com.daily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "健康检查", description = "系统健康状态检测")
@RestController
public class HealthController {

    @Operation(summary = "健康检查", description = "返回系统运行状态，用于监控探活和负载均衡健康检查")
    @GetMapping({"/health", "/"})
    public Map<String, Object> health() {
        return Map.of("status", "ok");
    }
}
