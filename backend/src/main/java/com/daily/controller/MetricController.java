package com.daily.controller;

import com.daily.entity.MetricDefinition;
import com.daily.service.MetricService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping
    public Map<String, Object> list() {
        List<MetricDefinition> metrics = metricService.listAll();
        return ok(metrics);
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody List<MetricDefinition> metrics) {
        List<MetricDefinition> saved = metricService.saveAll(metrics);
        return ok(saved);
    }
}
