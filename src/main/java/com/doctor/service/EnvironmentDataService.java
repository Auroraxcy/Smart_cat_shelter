package com.doctor.service;

import com.doctor.entity.EnvironmentData;
import java.time.LocalDateTime;
import java.util.List;

public interface EnvironmentDataService {
    EnvironmentData getLatestData();
    List<EnvironmentData> getHistoryByArea(String area);
    List<EnvironmentData> getHistoryByAreaAndTimeRange(String area, LocalDateTime start, LocalDateTime end);
    EnvironmentData createRecord(EnvironmentData data);
}
