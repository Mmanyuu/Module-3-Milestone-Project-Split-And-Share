package sg.edu.ntu.split_and_share.service;

import java.util.List;

import sg.edu.ntu.split_and_share.entity.Expense;

public interface ExpenseService {

  Expense addExpense(Expense expense, String username);

  Expense updateExpense(Long id, Expense newDetails);

  void deleteExpense(Long id);

  Expense getExpense(Long id);

  List<Expense> getAllExpenses();
}
