package com.doctor.service.impl;

import com.doctor.entity.HealthReminder;
import com.doctor.exception.BusinessException;
import com.doctor.repository.HealthReminderRepository;
import com.doctor.service.HealthReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HealthReminderServiceImpl implements HealthReminderService {

    @Autowired
    private HealthReminderRepository healthReminderRepository;

    @Override
    public List<HealthReminder> getAllReminders() {
        return healthReminderRepository.findAll();
    }

    @Override
    public List<HealthReminder> getPendingReminders() {
        return healthReminderRepository.findByCompletedFalseOrderByRemindTimeAsc();
    }

    @Override
    public List<HealthReminder> getRemindersByCatId(Long catId) {
        return healthReminderRepository.findByCatId(catId);
    }

    @Override
    public HealthReminder createReminder(HealthReminder reminder) {
        if (reminder.getCompleted() == null) {
            reminder.setCompleted(false);
        }
        return healthReminderRepository.save(reminder);
    }

    @Override
    public HealthReminder markCompleted(Long id) {
        HealthReminder reminder = healthReminderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "提醒不存在"));
        reminder.setCompleted(true);
        return healthReminderRepository.save(reminder);
    }

    @Override
    public void deleteReminder(Long id) {
        HealthReminder reminder = healthReminderRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "提醒不存在"));
        healthReminderRepository.delete(reminder);
    }
}
