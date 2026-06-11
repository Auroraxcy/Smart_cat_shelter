package com.doctor.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "feeding_records")
public class FeedingRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cat_id", nullable = false)
    private Cat cat;                // 关联的猫咪

    @Column(nullable = false)
    private LocalDateTime feedingTime;  // 喂食时间

    private String foodType;        // 食物类型
    private Double amount;          // 喂食量（克）

    @ManyToOne
    @JoinColumn(name = "keeper_id")
    private User keeper;            // 饲养员

    private String notes;           // 备注

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
