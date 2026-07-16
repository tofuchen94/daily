package com.daily.controller;

import com.daily.entity.MetricDefinition;
import com.daily.service.MetricService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "指标定义", description = "管理日报中录制的指标项，如销售额、客户数等")
@RestController
@RequestMapping("/api/metrics")
public class MetricController {

    private final MetricService metricService;

    public MetricController(MetricService metricService) {
        this.metricService = metricService;
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("data", data);
        return map;
    }

    @Operation(summary = "获取所有指标定义", description = "返回启用的指标列表，按 sort_order 排序")
    @GetMapping
    public Map<String, Object> list() {
        List<MetricDefinition> metrics = metricService.listAll();
        return ok(metrics);
    }

    @Operation(summary = "批量保存指标定义", description = "全量替换所有指标定义：传入完整的指标列表，系统自动增删以匹配传入数据")
    @PostMapping
    public Map<String, Object> save(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "指标定义列表，如 [{\"name\":\"销售额\",\"unit\":\"元\",\"sortOrder\":1}]")
                                    @RequestBody List<MetricDefinition> metrics) {
        List<MetricDefinition> saved = metricService.saveAll(metrics);
        return ok(saved);
    }
}
