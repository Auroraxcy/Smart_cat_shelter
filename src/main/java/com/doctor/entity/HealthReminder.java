package com.doctor.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "health_reminders")
public class HealthReminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;           // 提醒标题

    private String description;     // 提醒描述

    @ManyToOne
    @JoinColumn(name = "cat_id")
    private Cat cat;                // 关联的猫咪

    private LocalDateTime remindTime;  // 提醒时间

    private String priority;        // 优先级：HIGH, MEDIUM, LOW

    private Boolean completed;      // 是否已完成

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;         // 创建人

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
