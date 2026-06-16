package com.doctor.service;

import com.doctor.entity.FeedingRecord;
import java.time.LocalDateTime;
import java.util.List;

public interface FeedingRecordService {
    List<FeedingRecord> getAllRecords();
    List<FeedingRecord> getRecordsByCatId(Long catId);
    List<FeedingRecord> getRecordsByDateRange(LocalDateTime start, LocalDateTime end);
    Double getTodayTotalAmount();
    FeedingRecord createRecord(FeedingRecord record);
    void deleteRecord(Long id);
}
