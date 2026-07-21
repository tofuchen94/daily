package com.daily.service;

import com.daily.config.UserContext;
import com.daily.entity.DailyRecord;
import com.daily.entity.RecordMetric;
import com.daily.entity.Template;
import com.daily.mapper.DailyRecordMapper;
import com.daily.mapper.RecordMetricMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RecordService {

    private final DailyRecordMapper recordMapper;
    private final RecordMetricMapper metricMapper;
    private final TemplateService templateService;

    public RecordService(DailyRecordMapper recordMapper,
                         RecordMetricMapper metricMapper,
                         TemplateService templateService) {
        this.recordMapper = recordMapper;
        this.metricMapper = metricMapper;
        this.templateService = templateService;
    }

    private Long userId() {
        return UserContext.get();
    }

    public List<DailyRecord> listByMonth(String month) {
        Long uid = userId();
        List<DailyRecord> records = recordMapper.findByMonth(uid, month);
        for (DailyRecord r : records) {
            r.setMetrics(metricMapper.findByRecordId(r.getId()));
        }
        return records;
    }

    public DailyRecord getByDate(LocalDate date) {
        Long uid = userId();
        DailyRecord record = recordMapper.findByDate(uid, date);
        if (record != null) {
            record.setMetrics(metricMapper.findByRecordId(record.getId()));
        }
        return record;
    }

    @Transactional
    public DailyRecord save(LocalDate date, List<RecordMetric> metrics) {
        Long uid = userId();
        DailyRecord record = recordMapper.findByDate(uid, date);
        if (record == null) {
            record = new DailyRecord(date);
            record.setUserId(uid);
            recordMapper.insert(record);
        } else {
            record.setUpdatedAt(java.time.LocalDateTime.now());
            recordMapper.updateById(record);
        }

        // Replace metrics for this record
        metricMapper.deleteByRecordId(record.getId());
        if (metrics != null) {
            for (RecordMetric m : metrics) {
                m.setRecordId(record.getId());
                metricMapper.insert(m);
            }
        }

        record.setMetrics(metricMapper.findByRecordId(record.getId()));
        return record;
    }

    @Transactional
    public void delete(Long id) {
        metricMapper.deleteByRecordId(id);
        recordMapper.deleteById(id);
    }

    /**
     * Generate summary from template for a given date
     */
    public String generate(LocalDate date, Long templateId) {
        DailyRecord record = getByDate(date);

        Template template;
        if (templateId != null) {
            template = templateService.listAll().stream()
                    .filter(t -> t.getId().equals(templateId))
                    .findFirst().orElse(templateService.getDefault());
        } else {
            template = templateService.getDefault();
        }

        if (template == null) {
            return "请先在设置中创建模版";
        }

        String content = template.getContent();

        // Build metrics map: name -> RecordMetric
        Map<String, RecordMetric> metricMap = new HashMap<>();
        if (record != null && record.getMetrics() != null) {
            for (RecordMetric m : record.getMetrics()) {
                metricMap.put(m.getMetricName(), m);
            }
        }

        // Replace {date}
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy年M月d日");
        content = content.replace("{date}", date.format(fmt));

        // Replace metric placeholders: {name}, {name:值}, {name:单位}
        Pattern p = Pattern.compile("\\{([^}]+)\\}");
        Matcher m = p.matcher(content);
        StringBuffer sb = new StringBuffer();

        while (m.find()) {
            String key = m.group(1);
            if ("date".equals(key)) {
                m.appendReplacement(sb, date.format(fmt));
            } else if (key.contains(":")) {
                String[] parts = key.split(":", 2);
                String name = parts[0];
                String mode = parts[1];
                RecordMetric rm = metricMap.get(name);
                if (rm != null) {
                    if ("值".equals(mode)) {
                        m.appendReplacement(sb, Matcher.quoteReplacement(rm.getMetricValue()));
                    } else if ("单位".equals(mode)) {
                        m.appendReplacement(sb, Matcher.quoteReplacement(
                                rm.getUnit() != null ? rm.getUnit() : ""));
                    }
                } else {
                    m.appendReplacement(sb, "");
                }
            } else {
                RecordMetric rm = metricMap.get(key);
                if (rm != null) {
                    String val = rm.getMetricValue();
                    if (rm.getUnit() != null && !rm.getUnit().isEmpty()) {
                        val += rm.getUnit();
                    }
                    m.appendReplacement(sb, Matcher.quoteReplacement(val));
                } else {
                    m.appendReplacement(sb, "");
                }
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
