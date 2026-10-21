package com.barriodigital.demo.controller;

import com.barriodigital.demo.dto.KpiResponseDto;
import com.barriodigital.demo.repository.ReportEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportEventRepository repository;

    @GetMapping("/kpis")
    public ResponseEntity<KpiResponseDto> getKpis(@RequestParam(defaultValue = "last24h") String range) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);

        if ("last7d".equalsIgnoreCase(range)) {
            since = LocalDateTime.now().minusDays(7);
        }

        long totalEvents = repository.countTotalEventsSince(since);
        long createdRequests = repository.countCreatedRequestsSince(since);
        long completedRequests = repository.countCompletedRequestsSince(since);

        KpiResponseDto response = KpiResponseDto.builder()
                .totalEvents(totalEvents)
                .createdRequests(createdRequests)
                .completedRequests(completedRequests)
                .build();

        return ResponseEntity.ok(response);
    }
}