package com.doctor.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "environment_data")
public class EnvironmentData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double temperature;     // 温度（摄氏度）
    private Double humidity;        // 湿度（百分比）
    private Double airQuality;      // 空气质量指数
    private String area;            // 区域位置

    @ManyToOne
    @JoinColumn(name = "recorded_by")
    private User recordedBy;        // 记录人

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
