package cl.duoc.barriodigital.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestEventDTO {

    private Long requestId;
    private Long procedureId;
    private String oldStatus;
    private String newStatus;
    private LocalDateTime timestamp;
}