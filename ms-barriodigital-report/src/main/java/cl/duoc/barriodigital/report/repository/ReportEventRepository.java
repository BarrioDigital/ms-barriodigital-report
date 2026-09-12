package cl.duoc.barriodigital.report.repository;

import cl.duoc.barriodigital.report.entity.ReportEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportEventRepository extends JpaRepository<ReportEvent, Long> {

    boolean existsByEventKey(String eventKey);

    List<ReportEvent> findByEventTimestampBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}