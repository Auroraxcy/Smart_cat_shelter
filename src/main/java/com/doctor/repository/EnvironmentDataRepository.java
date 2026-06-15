package com.doctor.repository;

import com.doctor.entity.EnvironmentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnvironmentDataRepository extends JpaRepository<EnvironmentData, Long> {
    List<EnvironmentData> findByAreaOrderByCreateTimeDesc(String area);
    List<EnvironmentData> findByArea(String area);
}
