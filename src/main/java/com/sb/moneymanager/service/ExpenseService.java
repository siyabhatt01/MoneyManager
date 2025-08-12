package com.sb.moneymanager.service;

import com.sb.moneymanager.dto.CategoryDTO;
import com.sb.moneymanager.dto.ExpenseDTO;
import com.sb.moneymanager.entity.CategoryEntity;
import com.sb.moneymanager.entity.ExpenseEntity;
import com.sb.moneymanager.entity.ProfileEntity;
import com.sb.moneymanager.repository.CategoryRepository;
import com.sb.moneymanager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    //add new expense to the database
    public ExpenseDTO addExpense(ExpenseDTO dto)
    {
        ProfileEntity profile= profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found !"));
        ExpenseEntity newExpense=toEntity(dto,profile,category);
        newExpense= expenseRepository.save(newExpense);
        return toDTO(newExpense);

    }

    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser()
    {
       ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity>list= expenseRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        List<ExpenseDTO>dtos=new ArrayList<>();
        for(ExpenseEntity l: list)
        {
            dtos.add(toDTO(l));
        }
        return dtos;
    }

    public void deleteExpenses(Long expenseId)
    {
        ProfileEntity profile=profileService.getCurrentProfile();
        ExpenseEntity entity=expenseRepository.findById(expenseId)
                .orElseThrow(()->new RuntimeException("Expense not found"));
        if(!entity.getProfile().getId().equals(profile.getId()))
        {
            throw new RuntimeException("Unauthorized to delete this account");
        }
        expenseRepository.delete(entity);
    }

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser()
    {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepository.findTop5ByProfileIdOrderByDateDesc(currentProfile.getId());
        List<ExpenseDTO>dtos=new ArrayList<>();
        for(ExpenseEntity l: list)
        {
            dtos.add(toDTO(l));
        }
        return dtos;
    }

    public BigDecimal getTotalExpenseForCurrentUser()
    {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        BigDecimal totalExpense = expenseRepository.findTotalExpenseByProfileId(currentProfile.getId());
        return totalExpense!=null ? totalExpense :BigDecimal.ZERO;
    }

    //helper functions
    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profile, CategoryEntity category)
    {
        return ExpenseEntity.builder()
                .name(expenseDTO.getName())
                .amount(expenseDTO.getAmount())
                .category(category)
                .date(expenseDTO.getDate())
                .icon(expenseDTO.getIcon())
                .profile(profile)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity entity)
    {
        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory()!=null ? entity.getCategory().getId():null)
                .categoryName(entity.getCategory()!=null ? entity.getCategory().getName():null)
                .amount(entity.getAmount())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .date(entity.getDate())
                .build();

    }
}
