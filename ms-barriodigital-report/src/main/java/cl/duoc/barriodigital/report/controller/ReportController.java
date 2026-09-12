package cl.duoc.barriodigital.report.controller;

import cl.duoc.barriodigital.report.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    @GetMapping("/kpis")
    public ResponseEntity<Map<String, Object>> getKpis(
            @RequestParam(defaultValue = "last24h") String range
    ) {
        return ResponseEntity.ok(service.getKpis(range));
    }

    @GetMapping("/top-procedures")
    public ResponseEntity<Map<Long, Long>> getTopProcedures(
            @RequestParam(defaultValue = "last7d") String range
    ) {
        return ResponseEntity.ok(service.getTopProcedures(range));
    }
}