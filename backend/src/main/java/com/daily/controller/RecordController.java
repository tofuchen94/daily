package com.daily.controller;

import com.daily.entity.DailyRecord;
import com.daily.service.RecordService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    /**
     * GET /api/records?month=2026-06
     */
    @GetMapping
    public Map<String, Object> list(@RequestParam(required = false) String month) {
        if (month == null || month.isEmpty()) {
            month = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        List<DailyRecord> records = recordService.listByMonth(month);
        return ok(records);
    }

    /**
     * GET /api/records/2026-06-05
     */
    @GetMapping("/{date}")
    public Map<String, Object> get(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        DailyRecord record = recordService.getByDate(localDate);
        return ok(record);
    }

    /**
     * POST /api/records
     * Body: { "recordDate": "2026-06-05", "metrics": [...] }
     */
    // Map.of 不允许 null 值，用 HashMap 包装返回
    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("data", data);
        return map;
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody Map<String, Object> body) {
        LocalDate date = LocalDate.parse((String) body.get("recordDate"));
        @SuppressWarnings("unchecked")
        List<Map<String, String>> metricsRaw = (List<Map<String, String>>) body.get("metrics");

        List<com.daily.entity.RecordMetric> metrics = null;
        if (metricsRaw != null) {
            metrics = metricsRaw.stream().map(m -> new com.daily.entity.RecordMetric(
                    null,
                    m.get("metricName"),
                    m.get("metricValue"),
                    m.get("unit")
            )).toList();
        }

        DailyRecord saved = recordService.save(date, metrics);
        return ok(saved);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        recordService.delete(id);
        return Map.of("code", 0, "msg", "ok");
    }

    /**
     * POST /api/generate/2026-06-05?templateId=1
     */
    @PostMapping("/generate/{date}")
    public Map<String, Object> generate(@PathVariable String date,
                                         @RequestParam(required = false) Long templateId) {
        LocalDate localDate = LocalDate.parse(date);
        String summary = recordService.generate(localDate, templateId);
        return ok(summary);
    }
}
