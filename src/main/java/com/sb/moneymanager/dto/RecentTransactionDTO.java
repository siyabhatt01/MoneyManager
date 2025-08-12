package com.sb.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecentTransactionDTO {

    private Long id;
    private Long profileId;
    private String icon;;
    private String name;
    private BigDecimal amount;
    private LocalDate date;
    private String type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}