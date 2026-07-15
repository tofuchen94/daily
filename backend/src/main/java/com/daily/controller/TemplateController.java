package com.daily.controller;

import com.daily.entity.Template;
import com.daily.service.TemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @GetMapping
    public Map<String, Object> list() {
        List<Template> templates = templateService.listAll();
        return ok(templates);
    }

    @PostMapping
    public Map<String, Object> save(@RequestBody Template template) {
        Template saved = templateService.save(template);
        return ok(saved);
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        templateService.delete(id);
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("msg", "ok");
        return result;
    }
}
