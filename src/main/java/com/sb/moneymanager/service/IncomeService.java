package com.sb.moneymanager.service;

import com.sb.moneymanager.dto.ExpenseDTO;
import com.sb.moneymanager.dto.IncomeDTO;
import com.sb.moneymanager.entity.CategoryEntity;
import com.sb.moneymanager.entity.ExpenseEntity;
import com.sb.moneymanager.entity.IncomeEntity;
import com.sb.moneymanager.entity.ProfileEntity;
import com.sb.moneymanager.repository.CategoryRepository;
import com.sb.moneymanager.repository.ExpenseRepository;
import com.sb.moneymanager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;

    //add new expense to the database
    public IncomeDTO addIncome(IncomeDTO dto)
    {
        ProfileEntity profile= profileService.getCurrentProfile();
        CategoryEntity category=categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(()-> new RuntimeException("Category not found !"));
        IncomeEntity newExpense=toEntity(dto,profile,category);
        newExpense= incomeRepository.save(newExpense);
        return toDTO(newExpense);

    }

    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser()
    {
        ProfileEntity profile= profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate=now.withDayOfMonth(1);
        LocalDate endDate=now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity>list= incomeRepository.findByProfileIdAndDateBetween(profile.getId(),startDate,endDate);
        List<IncomeDTO>dtos=new ArrayList<>();
        for(IncomeEntity l: list)
        {
            dtos.add(toDTO(l));
        }
        return dtos;
    }

    public void deleteIncomes(Long incomeId)
    {
        ProfileEntity profile=profileService.getCurrentProfile();
        IncomeEntity entity=incomeRepository.findById(incomeId)
                .orElseThrow(()->new RuntimeException("Income not found"));
        if(!entity.getProfile().getId().equals(profile.getId()))
        {
            throw new RuntimeException("Unauthorized to delete this account");
        }
        incomeRepository.delete(entity);
    }

    public List<IncomeDTO> getLatest5IncomesForCurrentUser()
    {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findTop5ByProfileIdOrderByDateDesc(currentProfile.getId());
        List<IncomeDTO>dtos=new ArrayList<>();
        for(IncomeEntity l: list)
        {
            dtos.add(toDTO(l));
        }
        return dtos;
    }

    //filter income
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort)
    {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(currentProfile.getId(), startDate, endDate, keyword, sort);
        List<IncomeDTO>dtos=new ArrayList<>();
        for(IncomeEntity l: list)
        {
            dtos.add(toDTO(l));
        }
        return dtos;
    }

    public BigDecimal getTotalIncomesForCurrentUser()
    {
        ProfileEntity currentProfile = profileService.getCurrentProfile();
        BigDecimal totalIncome = incomeRepository.findTotalExpenseByProfileId(currentProfile.getId());
        return totalIncome!=null ? totalIncome :BigDecimal.ZERO;
    }

    //helper functions
    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profile, CategoryEntity category)
    {
        return IncomeEntity.builder()
                .name(incomeDTO.getName())
                .amount(incomeDTO.getAmount())
                .category(category)
                .date(incomeDTO.getDate())
                .icon(incomeDTO.getIcon())
                .profile(profile)
                .build();
    }

    private IncomeDTO toDTO(IncomeEntity entity)
    {
        return IncomeDTO.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory()!=null ? entity.getCategory().getId():null)
                .categoryName(entity.getCategory()!=null ? entity.getCategory().getName():null)
                .amount(entity.getAmount())
                .updatedAt(entity.getUpdatedAt())
                .createdAt(entity.getCreatedAt())
                .build();

    }
}
