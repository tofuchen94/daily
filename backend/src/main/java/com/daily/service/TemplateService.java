package com.daily.service;

import com.daily.entity.Template;
import com.daily.mapper.TemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemplateService {

    private final TemplateMapper templateMapper;

    public TemplateService(TemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
    }

    public List<Template> listAll() {
        return templateMapper.selectList(null);
    }

    @Transactional
    public Template save(Template template) {
        if (template.getIsDefault() != null && template.getIsDefault() == 1) {
            templateMapper.clearDefault();
        }
        if (template.getId() != null && templateMapper.selectById(template.getId()) != null) {
            templateMapper.updateById(template);
        } else {
            templateMapper.insert(template);
        }
        return template;
    }

    @Transactional
    public void delete(Long id) {
        templateMapper.deleteById(id);
    }

    public Template getDefault() {
        return templateMapper.findDefault();
    }

    @Transactional
    public void setDefault(Long id) {
        templateMapper.clearDefault();
        Template tpl = templateMapper.selectById(id);
        if (tpl != null) {
            tpl.setIsDefault(1);
            templateMapper.updateById(tpl);
        }
    }
}
