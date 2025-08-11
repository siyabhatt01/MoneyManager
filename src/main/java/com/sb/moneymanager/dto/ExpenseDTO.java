package com.sb.moneymanager.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseDTO {
    private Long id;
    private String name;
    private String icon;
    private LocalDate date;
    private String categoryName;
    private Long categoryId;
    private BigDecimal amount;
    private LocalDateTime createdAt;;
    private LocalDateTime updatedAt;

}
