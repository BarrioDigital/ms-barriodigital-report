package com.barriodigital.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KpiResponseDto {
    private long totalEvents;
    private long createdRequests;
    private long completedRequests;
}