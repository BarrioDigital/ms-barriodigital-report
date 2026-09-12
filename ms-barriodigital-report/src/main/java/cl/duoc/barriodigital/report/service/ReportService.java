package cl.duoc.barriodigital.report.service;

import cl.duoc.barriodigital.report.dto.RequestEventDTO;
import cl.duoc.barriodigital.report.entity.ReportEvent;
import cl.duoc.barriodigital.report.repository.ReportEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportEventRepository repository;

    @KafkaListener(
            topics = "requests.events",
            groupId = "report-service",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(RequestEventDTO event) {

        ReportEvent reportEvent = ReportEvent.builder()
                .requestId(event.getRequestId())
                .procedureId(event.getProcedureId())
                .oldStatus(event.getOldStatus())
                .newStatus(event.getNewStatus())
                .eventTimestamp(event.getTimestamp())
                .build();

        repository.save(reportEvent);
    }

    public Map<String, Object> getKpis(String range) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        if ("last7d".equalsIgnoreCase(range)) {
            start = now.minusDays(7);
        } else {
            start = now.minusHours(24);
        }

        List<ReportEvent> events =
                repository.findByEventTimestampBetween(start, now);

        long totalEvents = events.size();

        long createdRequests = events.stream()
                .filter(e -> "INGRESADO".equalsIgnoreCase(e.getNewStatus()))
                .count();

        long completedRequests = events.stream()
                .filter(e -> "FINALIZADO".equalsIgnoreCase(e.getNewStatus()))
                .count();

        Map<String, Object> response = new HashMap<>();

        response.put("range", range);
        response.put("totalEvents", totalEvents);
        response.put("createdRequests", createdRequests);
        response.put("completedRequests", completedRequests);

        return response;
    }

    public Map<Long, Long> getTopProcedures(String range) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start;

        if ("last24h".equalsIgnoreCase(range)) {
            start = now.minusHours(24);
        } else {
            start = now.minusDays(7);
        }

        List<ReportEvent> events =
                repository.findByEventTimestampBetween(start, now);

        return events.stream()
                .filter(e -> e.getProcedureId() != null)
                .collect(Collectors.groupingBy(
                        ReportEvent::getProcedureId,
                        Collectors.counting()
                ));
    }
}