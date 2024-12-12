package sg.edu.ntu.split_and_share.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.service.ExpenseService;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

  private ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  // Create a new expense
  // http://localhost:8080/api/expense/{username}/add
  @PostMapping("/{username}/add")
  public ResponseEntity<Expense> addExpense(@PathVariable String username, @Valid @RequestBody Expense expense) {
    Expense createdExpense = expenseService.addExpense(expense, username);
    return new ResponseEntity<>(createdExpense, HttpStatus.CREATED);
  }

  // Get an expense by ID
  // http://localhost:8080/api/expense/{id}
  @GetMapping("/{id}")
  public ResponseEntity<Expense> getExpense(@PathVariable Long id) {
    Expense expense = expenseService.getExpense(id);
    return new ResponseEntity<>(expense, HttpStatus.OK);
  }

  // Get all expenses
  // http://localhost:8080/api/expense
  @GetMapping("")
  public ResponseEntity<List<Expense>> getAllExpenses() {
    List<Expense> expenses = expenseService.getAllExpenses();
    return new ResponseEntity<>(expenses, HttpStatus.OK);
  }

  // Edit an expense
  // http://localhost:8080/api/expense/{id}
  @PutMapping("/{id}")
  public ResponseEntity<Expense> updateExpense(@PathVariable Long id, @Valid @RequestBody Expense newDetails) {
    Expense updatedExpense = expenseService.updateExpense(id, newDetails);
    return new ResponseEntity<>(updatedExpense, HttpStatus.OK);
  }

  // Delete an expense
  // http://localhost:8080/api/expense/{id}
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteExpense(@PathVariable Long id) {
    expenseService.deleteExpense(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
