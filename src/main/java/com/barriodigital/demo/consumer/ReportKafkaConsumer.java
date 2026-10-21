package com.barriodigital.demo.consumer;

import com.barriodigital.demo.dto.KafkaRequestEventDto;
import com.barriodigital.demo.model.ReportEvent;
import com.barriodigital.demo.repository.ReportEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ReportKafkaConsumer {

    @Autowired
    private ReportEventRepository repository;

    @KafkaListener(topics = "requests.events", groupId = "report-group")
    public void consumeRequestEvent(KafkaRequestEventDto event) {
        try {
            LocalDateTime eventTime = (event.getTimestamp() != null)
                    ? LocalDateTime.ofInstant(Instant.ofEpochMilli(event.getTimestamp()), ZoneId.systemDefault())
                    : LocalDateTime.now();

            ReportEvent report = ReportEvent.builder()
                    .requestId(event.getRequestId())
                    .oldStatus(event.getOldStatus())
                    .newStatus(event.getNewStatus())
                    .citizenId(event.getCitizenId())
                    .createdAt(eventTime)
                    .build();

            repository.save(report);
            System.out.println("[ms-report] Evento registrado para analítica. Trámite ID: " + event.getRequestId());
        } catch (Exception e) {
            System.err.println("[ms-report] Error procesando evento de reporte: " + e.getMessage());
        }
    }
}