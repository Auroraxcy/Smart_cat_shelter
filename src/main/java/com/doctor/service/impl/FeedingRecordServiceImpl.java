package com.doctor.service.impl;

import com.doctor.entity.FeedingRecord;
import com.doctor.exception.BusinessException;
import com.doctor.repository.FeedingRecordRepository;
import com.doctor.service.FeedingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class FeedingRecordServiceImpl implements FeedingRecordService {

    @Autowired
    private FeedingRecordRepository feedingRecordRepository;

    @Override
    public List<FeedingRecord> getAllRecords() {
        return feedingRecordRepository.findAll();
    }

    @Override
    public List<FeedingRecord> getRecordsByCatId(Long catId) {
        return feedingRecordRepository.findByCatIdOrderByFeedingTimeDesc(catId);
    }

    @Override
    public List<FeedingRecord> getRecordsByDateRange(LocalDateTime start, LocalDateTime end) {
        return feedingRecordRepository.findByFeedingTimeBetween(start, end);
    }

    @Override
    public Double getTodayTotalAmount() {
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        List<FeedingRecord> records = feedingRecordRepository.findByFeedingTimeBetween(start, end);
        return records.stream()
                .mapToDouble(r -> r.getAmount() != null ? r.getAmount() : 0.0)
                .sum();
    }

    @Override
    public FeedingRecord createRecord(FeedingRecord record) {
        if (record.getFeedingTime() == null) {
            record.setFeedingTime(LocalDateTime.now());
        }
        return feedingRecordRepository.save(record);
    }

    @Override
    public void deleteRecord(Long id) {
        FeedingRecord record = feedingRecordRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "记录不存在"));
        feedingRecordRepository.delete(record);
    }
}
