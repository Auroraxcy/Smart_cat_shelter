package com.doctor.service;

import com.doctor.entity.HealthReminder;
import java.util.List;

public interface HealthReminderService {
    List<HealthReminder> getAllReminders();
    List<HealthReminder> getPendingReminders();
    List<HealthReminder> getRemindersByCatId(Long catId);
    HealthReminder createReminder(HealthReminder reminder);
    HealthReminder markCompleted(Long id);
    void deleteReminder(Long id);
}
