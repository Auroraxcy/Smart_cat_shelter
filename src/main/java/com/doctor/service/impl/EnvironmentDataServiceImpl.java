package com.doctor.service.impl;

import com.doctor.entity.EnvironmentData;
import com.doctor.repository.EnvironmentDataRepository;
import com.doctor.service.EnvironmentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnvironmentDataServiceImpl implements EnvironmentDataService {

    @Autowired
    private EnvironmentDataRepository environmentDataRepository;

    @Override
    public EnvironmentData getLatestData() {
        List<EnvironmentData> all = environmentDataRepository.findAll();
        return all.isEmpty() ? null : all.get(all.size() - 1);
    }

    @Override
    public List<EnvironmentData> getHistoryByArea(String area) {
        return environmentDataRepository.findByAreaOrderByCreateTimeDesc(area);
    }

    @Override
    public List<EnvironmentData> getHistoryByAreaAndTimeRange(String area, LocalDateTime start, LocalDateTime end) {
        List<EnvironmentData> all = environmentDataRepository.findByArea(area);
        return all.stream()
                .filter(data -> data.getCreateTime().isAfter(start) && data.getCreateTime().isBefore(end))
                .collect(Collectors.toList());
    }

    @Override
    public EnvironmentData createRecord(EnvironmentData data) {
        return environmentDataRepository.save(data);
    }
}
