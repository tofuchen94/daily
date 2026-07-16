package com.daily.controller;

import com.daily.entity.Template;
import com.daily.service.TemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "日报模板", description = "管理日报摘要的格式化模板")
@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService templateService;

    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    private Map<String, Object> ok(Object data) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", 0);
        map.put("data", data);
        return map;
    }

    @Operation(summary = "获取所有模板", description = "返回全部日报模板列表")
    @GetMapping
    public Map<String, Object> list() {
        List<Template> templates = templateService.listAll();
        return ok(templates);
    }

    @Operation(summary = "创建或更新模板", description = "保存一个日报模板（名称+内容），ID 存在时更新，不存在时新增")
    @PostMapping
    public Map<String, Object> save(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "模板对象，如 {\"name\":\"简洁版\",\"content\":\"【业绩日报】{date}\\n{销售额}\\n{备注}\"}")
                                    @RequestBody Template template) {
        Template saved = templateService.save(template);
        return ok(saved);
    }

    @Operation(summary = "删除模板", description = "按 ID 删除指定模板")
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@Parameter(description = "模板 ID") @PathVariable Long id) {
        templateService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "ok");
        return result;
    }
}
