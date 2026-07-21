package com.daily.service;

import com.daily.config.UserContext;
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

    private Long userId() {
        return UserContext.get();
    }

    public List<Template> listAll() {
        Long uid = userId();
        return templateMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Template>()
                        .eq(Template::getUserId, uid)
                        .orderByAsc(Template::getId));
    }

    @Transactional
    public Template save(Template template) {
        Long uid = userId();
        if (template.getIsDefault() != null && template.getIsDefault() == 1) {
            templateMapper.clearDefault(uid);
        }
        if (template.getId() != null && templateMapper.selectById(template.getId()) != null) {
            templateMapper.updateById(template);
        } else {
            template.setUserId(uid);
            templateMapper.insert(template);
        }
        return template;
    }

    @Transactional
    public void delete(Long id) {
        templateMapper.deleteById(id);
    }

    public Template getDefault() {
        return templateMapper.findDefault(userId());
    }

    @Transactional
    public void setDefault(Long id) {
        Long uid = userId();
        templateMapper.clearDefault(uid);
        Template tpl = templateMapper.selectById(id);
        if (tpl != null) {
            tpl.setIsDefault(1);
            templateMapper.updateById(tpl);
        }
    }
}
