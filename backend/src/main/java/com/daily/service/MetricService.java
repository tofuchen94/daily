package com.daily.service;

import com.daily.config.UserContext;
import com.daily.entity.MetricDefinition;
import com.daily.mapper.MetricDefinitionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MetricService {

    private final MetricDefinitionMapper metricMapper;

    public MetricService(MetricDefinitionMapper metricMapper) {
        this.metricMapper = metricMapper;
    }

    private Long userId() {
        return UserContext.get();
    }

    public List<MetricDefinition> listAll() {
        return metricMapper.findAllOrdered(userId());
    }

    @Transactional
    public List<MetricDefinition> saveAll(List<MetricDefinition> metrics) {
        Long uid = userId();
        metricMapper.deleteAllByUserId(uid);
        if (metrics != null && !metrics.isEmpty()) {
            for (MetricDefinition m : metrics) {
                m.setUserId(uid);
                metricMapper.insert(m);
            }
        }
        return metricMapper.findAllOrdered(uid);
    }
}
