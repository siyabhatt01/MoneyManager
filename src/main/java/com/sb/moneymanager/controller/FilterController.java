package com.sb.moneymanager.controller;

import com.sb.moneymanager.dto.ExpenseDTO;
import com.sb.moneymanager.dto.FilterDTO;
import com.sb.moneymanager.dto.IncomeDTO;
import com.sb.moneymanager.service.ExpenseService;
import com.sb.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequestMapping("/filter")
@RestController
@RequiredArgsConstructor
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO)
    {
        LocalDate startDate=filterDTO.getStartDate()!=null ? filterDTO.getStartDate() : LocalDate.MIN;
        LocalDate endDate= filterDTO.getEndDate()!=null ? filterDTO.getEndDate() : LocalDate.now();
        String keyword=filterDTO.getKeyword()!=null ? filterDTO.getKeyword() : "";
        String sortField = filterDTO.getSortField() != null ? filterDTO.getSortField() : "date";
        Sort.Direction direction= "desc".equalsIgnoreCase(filterDTO.getSortOrder())?Sort.Direction.DESC :  Sort.Direction.ASC;
        Sort sort = Sort.by(direction,sortField);
        if("income".equals(filterDTO.getType()))
        {
            List<IncomeDTO> incomeDTOS = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomeDTOS);
        }
        else if("expense".equals(filterDTO.getType()))
        {
            List<ExpenseDTO> expenseDTOS = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenseDTOS);
        }
        else return ResponseEntity.badRequest().body("Must be of type 'income' or 'expense' ");
    }
}
