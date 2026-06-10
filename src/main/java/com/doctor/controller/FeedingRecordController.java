package com.doctor.controller;

import com.doctor.dto.ApiResponse;
import com.doctor.entity.FeedingRecord;
import com.doctor.service.FeedingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feeding-records")
public class FeedingRecordController {

    @Autowired
    private FeedingRecordService feedingRecordService;

    @GetMapping
    public ApiResponse<?> getAllRecords() {
        try {
            List<FeedingRecord> records = feedingRecordService.getAllRecords();
            return ApiResponse.success(records);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/cat/{catId}")
    public ApiResponse<?> getRecordsByCatId(@PathVariable Long catId) {
        try {
            List<FeedingRecord> records = feedingRecordService.getRecordsByCatId(catId);
            return ApiResponse.success(records);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/range")
    public ApiResponse<?> getRecordsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            List<FeedingRecord> records = feedingRecordService.getRecordsByDateRange(start, end);
            return ApiResponse.success(records);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/today-total")
    public ApiResponse<?> getTodayTotalAmount() {
        try {
            Double total = feedingRecordService.getTodayTotalAmount();
            Map<String, Object> data = new HashMap<>();
            data.put("totalAmount", total);
            return ApiResponse.success(data);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<?> createRecord(@RequestBody FeedingRecord record) {
        try {
            FeedingRecord createdRecord = feedingRecordService.createRecord(record);
            return ApiResponse.success("添加成功", createdRecord);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteRecord(@PathVariable Long id) {
        try {
            feedingRecordService.deleteRecord(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
