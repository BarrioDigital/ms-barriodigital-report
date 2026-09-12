package cl.duoc.barriodigital.report.service;

import cl.duoc.barriodigital.report.dto.RequestEventDTO;
import cl.duoc.barriodigital.report.entity.ReportEvent;
import cl.duoc.barriodigital.report.repository.ReportEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

        String eventKey =
                "request-" + event.getRequestId()
                        + "-" + event.getNewStatus()
                        + "-" + event.getTimestamp();

        if (repository.existsByEventKey(eventKey)) {
            return;
        }

        ReportEvent reportEvent = ReportEvent.builder()
                .eventKey(eventKey)
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
        LocalDateTime start = resolveStartDate(range, now);

        List<ReportEvent> events =
                repository.findByEventTimestampBetween(start, now);

        long totalEvents = events.size();

        long createdRequests = events.stream()
                .filter(event ->
                        "INGRESADO".equalsIgnoreCase(event.getNewStatus())
                )
                .count();

        long completedRequests = events.stream()
                .filter(event ->
                        "FINALIZADO".equalsIgnoreCase(event.getNewStatus())
                )
                .count();

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("range", range);
        response.put("totalEvents", totalEvents);
        response.put("createdRequests", createdRequests);
        response.put("completedRequests", completedRequests);

        return response;
    }

    public Map<Long, Long> getTopProcedures(String range) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = resolveStartDate(range, now);

        List<ReportEvent> events =
                repository.findByEventTimestampBetween(start, now);

        return events.stream()
                .filter(event -> event.getProcedureId() != null)
                .collect(Collectors.groupingBy(
                        ReportEvent::getProcedureId,
                        Collectors.counting()
                ));
    }

    private LocalDateTime resolveStartDate(
            String range,
            LocalDateTime now
    ) {

        if ("last7d".equalsIgnoreCase(range)) {
            return now.minusDays(7);
        }

        return now.minusHours(24);
    }
}