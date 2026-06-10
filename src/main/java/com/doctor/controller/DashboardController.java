package com.doctor.controller;

import com.doctor.dto.ApiResponse;
import com.doctor.service.CatService;
import com.doctor.service.FeedingRecordService;
import com.doctor.service.HealthReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private CatService catService;

    @Autowired
    private FeedingRecordService feedingRecordService;

    @Autowired
    private HealthReminderService healthReminderService;

    @GetMapping("/stats")
    public ApiResponse<?> getDashboardStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // 猫咪总数
            stats.put("totalCats", catService.getAllCats().size());
            
            // 待办提醒数量
            stats.put("pendingReminders", healthReminderService.getPendingReminders().size());
            
            // 今日喂食总量
            stats.put("todayFeedingAmount", feedingRecordService.getTodayTotalAmount());
            
            return ApiResponse.success(stats);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
