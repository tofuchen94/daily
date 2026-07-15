package com.daily.service;

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

    public List<MetricDefinition> listAll() {
        return metricMapper.findAllOrdered();
    }

    @Transactional
    public List<MetricDefinition> saveAll(List<MetricDefinition> metrics) {
        metricMapper.deleteAll();
        if (metrics != null && !metrics.isEmpty()) {
            for (MetricDefinition m : metrics) {
                metricMapper.insert(m);
            }
        }
        return metricMapper.findAllOrdered();
    }
}
