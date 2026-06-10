package com.doctor.controller;

import com.doctor.dto.ApiResponse;
import com.doctor.entity.EnvironmentData;
import com.doctor.service.EnvironmentDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/environment")
public class EnvironmentDataController {

    @Autowired
    private EnvironmentDataService environmentDataService;

    @GetMapping("/latest")
    public ApiResponse<?> getLatestData() {
        try {
            EnvironmentData data = environmentDataService.getLatestData();
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/history")
    public ApiResponse<?> getHistory(
            @RequestParam String area,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<EnvironmentData> data;
            if (start != null && end != null) {
                data = environmentDataService.getHistoryByAreaAndTimeRange(area, start, end);
            } else {
                data = environmentDataService.getHistoryByArea(area);
            }
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<?> createRecord(@RequestBody EnvironmentData data) {
        try {
            EnvironmentData createdData = environmentDataService.createRecord(data);
            return ApiResponse.success("录入成功", createdData);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
