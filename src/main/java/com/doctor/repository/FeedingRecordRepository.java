package com.doctor.repository;

import com.doctor.entity.FeedingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeedingRecordRepository extends JpaRepository<FeedingRecord, Long> {
    List<FeedingRecord> findByCatIdOrderByFeedingTimeDesc(Long catId);
    List<FeedingRecord> findByFeedingTimeBetween(LocalDateTime start, LocalDateTime end);
    List<FeedingRecord> findByKeeperId(Long keeperId);
}
