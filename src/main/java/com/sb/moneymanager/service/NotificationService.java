package com.sb.moneymanager.service;

import com.sb.moneymanager.dto.ExpenseDTO;
import com.sb.moneymanager.entity.ProfileEntity;
import com.sb.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${money.manager.frontend.url}")
    private String frontendUrl;

    @Scheduled(cron = "0 0 22 * * *",zone = "IST")// everyday at 10 pm
//    @Scheduled(cron = "0 * * * * *",zone = "IST")//every minute
    public void sendDailyIncomeExpensesReminder()
    {
        log.info("Job started : sendDailyIncomeExpenseReminder()");
        List<ProfileEntity>  profiles=profileRepository.findAll();
        for(ProfileEntity profile : profiles)
        {
            String body= "Hii "+ profile.getFullName()+" ,<br><br>"
                    + "This is a friendly reminder to add your income and expenses for today in Money Manager. <br><br>"
                    + "<a href="+frontendUrl+" style='display:inline-block; padding:10px 20px; background-color:#4CAF50; color:#fff; text-decoration:none; border-radius:5px; font-weight:bold;'>Go to Money Manager </a>"
                    +"<br><br> Best regards,<br>Money Manager Team";
            emailService.sendEmail(profile.getEmail(), "Daily reminder : Add your income and expenses",body);
            log.info("Job finished : sendDailyIncomeExpenseReminder()");

        }

    }

//      @Scheduled(cron = "0 * * * * *",zone = "IST")//every minute
    @Scheduled(cron = "0 0 23 * * *",zone = "IST")// everyday at 10 pm
    public void sendDailyExpenseSummary()
    {
        log.info("Job started : sendDailyExpenseSummary()");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for(ProfileEntity profile : profiles)
        {
            List<ExpenseDTO> todaysExpenses = expenseService.getExpenseForUserOnDate(profile.getId(), LocalDate.now());
            if(!todaysExpenses.isEmpty())
            {
                StringBuilder table=new StringBuilder();
                table.append("<table style='border-collapse:collapse; width:100%;'>");
                table.append("<tr style='background-color:#f2f2f2;'> <th style='border:1px solid #ddd;padding:8px;'>S.No.</th><th style='border:1px solid #ddd; padding:8px;'>Name</th><th style='border 1px solid #ddd;padding:8px;'>Amount</th><th style='border 1px solid #ddd;padding:8px;'>Category</th></tr>");
                int i=1;
                for(ExpenseDTO expenseDTO: todaysExpenses)
                {
                    table.append("<tr>");
                    table.append("<td style='border 1px solid #ddd;padding:8px;'>").append(i++).append("</td>");
                    table.append("<td style='border 1px solid #ddd;padding:8px;'>").append(expenseDTO.getName()).append("</td>");
                    table.append("<td style='border 1px solid #ddd;padding:8px;'>").append(expenseDTO.getAmount()).append("</td>");
                    table.append("<td style='border 1px solid #ddd;padding:8px;'>").append(expenseDTO.getCategoryId()!=null ? expenseDTO.getCategoryId() :"N/A").append("</td>");
                    table.append("</tr>");
                }
                table.append("</table>");
                String body="Hii "+profile.getFullName()+" ,<br><br>Here is a summary of your expenses for today : <br><br>"+table+"<br><br> best regards, <br> Money manager Team";
                emailService.sendEmail(profile.getEmail(), "Your daily Expense Summary",body);
            }
        }
        log.info("Job finished : sendDailyExpenseSummary()");

    }
}
