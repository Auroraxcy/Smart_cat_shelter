package com.doctor.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "cats")
public class Cat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String breed;          // 品种
    private String color;          // 颜色
    private Integer age;           // 年龄
    private String gender;         // 性别
    private Double weight;         // 体重
    private String healthStatus;   // 健康状态
    private String photo;          // 照片URL
    private String description;    // 描述

    @CreationTimestamp
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
