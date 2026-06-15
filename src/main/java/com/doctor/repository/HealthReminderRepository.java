package com.doctor.repository;

import com.doctor.entity.HealthReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthReminderRepository extends JpaRepository<HealthReminder, Long> {
    List<HealthReminder> findByCatId(Long catId);
    List<HealthReminder> findByCompletedFalse();
    List<HealthReminder> findByCreatedBy_Id(Long userId);
    List<HealthReminder> findByCompletedFalseOrderByRemindTimeAsc();
}
