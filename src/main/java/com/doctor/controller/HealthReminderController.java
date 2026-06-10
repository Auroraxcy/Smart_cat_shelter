package com.doctor.controller;

import com.doctor.dto.ApiResponse;
import com.doctor.entity.HealthReminder;
import com.doctor.service.HealthReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
public class HealthReminderController {

    @Autowired
    private HealthReminderService healthReminderService;

    @GetMapping
    public ApiResponse<?> getAllReminders() {
        try {
            List<HealthReminder> reminders = healthReminderService.getAllReminders();
            return ApiResponse.success(reminders);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ApiResponse<?> getPendingReminders() {
        try {
            List<HealthReminder> reminders = healthReminderService.getPendingReminders();
            return ApiResponse.success(reminders);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @GetMapping("/cat/{catId}")
    public ApiResponse<?> getRemindersByCatId(@PathVariable Long catId) {
        try {
            List<HealthReminder> reminders = healthReminderService.getRemindersByCatId(catId);
            return ApiResponse.success(reminders);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @PostMapping
    public ApiResponse<?> createReminder(@RequestBody HealthReminder reminder) {
        try {
            HealthReminder createdReminder = healthReminderService.createReminder(reminder);
            return ApiResponse.success("添加成功", createdReminder);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<?> markCompleted(@PathVariable Long id) {
        try {
            HealthReminder reminder = healthReminderService.markCompleted(id);
            return ApiResponse.success("标记完成", reminder);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<?> deleteReminder(@PathVariable Long id) {
        try {
            healthReminderService.deleteReminder(id);
            return ApiResponse.success("删除成功", null);
        } catch (Exception e) {
            return ApiResponse.error(500, e.getMessage());
        }
    }
}
