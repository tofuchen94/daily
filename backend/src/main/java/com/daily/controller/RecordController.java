package com.daily.controller;

import com.daily.entity.DailyRecord;
import com.daily.service.RecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "日报记录", description = "每日业绩记录的增删改查与报表生成")
@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @Operation(summary = "按月查询记录", description = "按月份获取每日记录列表，不传月份则默认当月")
    @GetMapping
    public Map<String, Object> list(@Parameter(description = "月份，格式 yyyy-MM，如 2026-06") @RequestParam(required = false) String month) {
        if (month == null || month.isEmpty()) {
            month = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        List<DailyRecord> records = recordService.listByMonth(month);
        return ok(records);
    }

    @Operation(summary = "按日期查询记录", description = "根据具体日期（yyyy-MM-dd）获取某一天的记录详情")
    @GetMapping("/{date}")
    public Map<String, Object> get(@Parameter(description = "日期，格式 yyyy-MM-dd") @PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        DailyRecord record = recordService.getByDate(localDate);
        return ok(record);
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("data", data);
        return map;
    }

    @Operation(summary = "创建或更新记录", description = "保存某一天的日报记录，包含该日各指标的值列表；已有记录则覆盖")
    @PostMapping
    public Map<String, Object> save(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "请求体: {\"recordDate\":\"2026-06-05\",\"metrics\":[{\"metricName\":\"销售额\",\"metricValue\":\"10000\",\"unit\":\"元\"}]}")
                                    @RequestBody Map<String, Object> body) {
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

    @Operation(summary = "删除记录", description = "按 ID 删除一条日报记录及其关联的指标数据")
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@Parameter(description = "记录 ID") @PathVariable Long id) {
        recordService.delete(id);
        return Map.of("code", 0, "msg", "ok");
    }

    @Operation(summary = "生成日报摘要", description = "根据日期和模板生成格式化日报文本，用于复制到其他系统或群发")
    @PostMapping("/generate/{date}")
    public Map<String, Object> generate(@Parameter(description = "日期，格式 yyyy-MM-dd") @PathVariable String date,
                                         @Parameter(description = "模板 ID，不传则使用默认模板") @RequestParam(required = false) Long templateId) {
        LocalDate localDate = LocalDate.parse(date);
        String summary = recordService.generate(localDate, templateId);
        return ok(summary);
    }
}
