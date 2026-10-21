package com.barriodigital.demo.repository;

import com.barriodigital.demo.model.ReportEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface ReportEventRepository extends JpaRepository<ReportEvent, Long> {

    @Query("SELECT COUNT(e) FROM ReportEvent e WHERE e.createdAt >= :startDate")
    long countTotalEventsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(e) FROM ReportEvent e WHERE e.oldStatus IS NULL AND e.createdAt >= :startDate")
    long countCreatedRequestsSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(e) FROM ReportEvent e WHERE e.newStatus = 'RESUELTO' AND e.createdAt >= :startDate")
    long countCompletedRequestsSince(@Param("startDate") LocalDateTime startDate);
}