package in.business.moneymanager.service;

import in.business.moneymanager.dto.ExpenseDTO;
import in.business.moneymanager.dto.IncomeDTO;
import in.business.moneymanager.dto.RecentTransactionDTO;
import in.business.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashboardData() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> data = new LinkedHashMap<>();

        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                        latestIncomes.stream().map(income -> RecentTransactionDTO.builder()
                                .id(income.getId())
                                .profileId(profile.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .date(income.getDate())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("income")
                                .build()
                        ),
                        latestExpenses.stream().map(expense -> RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .date(expense.getDate())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("expense")
                                .build()
                        )
                )
                .sorted((a, b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if (cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null) {
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
                }).collect(Collectors.toList());

        data.put(
                "totalBalance", incomeService.getTotalIncomeForCurrentUser()
                        .subtract(expenseService.getTotalExpenseForCurrentUser())
        );
        data.put("totalIncome", incomeService.getTotalIncomeForCurrentUser());
        data.put("totalExpense", expenseService.getTotalExpenseForCurrentUser());
        data.put("recent5Expenses", latestExpenses);
        data.put("recent5Incomes", latestIncomes);
        data.put("recentTransactions", recentTransactions);

        return data;
    }
}
