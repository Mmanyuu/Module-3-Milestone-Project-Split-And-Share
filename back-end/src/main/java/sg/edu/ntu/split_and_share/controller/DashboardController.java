package sg.edu.ntu.split_and_share.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.service.DashboardService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  // Get total sum of all expenses
  // http://localhost:8080/api/dashboard/{username}/total-sum
  @GetMapping("/{username}/total-sum")
  public ResponseEntity<Double> calculateTotalSum(@PathVariable String username) {
    double totalSum = dashboardService.calculateTotalSum(username);
    return new ResponseEntity<>(totalSum, HttpStatus.OK);
  }

  // Get total sum of each expense type
  // http://localhost:8080/api/dashboard/{username}/sum-by-type
  @GetMapping("/{username}/sum-by-type")
  public ResponseEntity<Map<String, Double>> sumExpensesByType(@PathVariable String username) {
    Map<String, Double> expensesByType = dashboardService.sumExpensesByType(username);
    return new ResponseEntity<>(expensesByType, HttpStatus.OK);
  }

  // Count the number of each expense type
  // http://localhost:8080/api/dashboard/{username}/count-by-type
  @GetMapping("/{username}/count-by-type")
  public ResponseEntity<Map<String, Long>> countExpensesByType(@PathVariable String username) {
    Map<String, Long> expenseCountByType = dashboardService.countExpensesByType(username);
    return ResponseEntity.ok(expenseCountByType);
  }

  // Count the grand total number of expenses
  // http://localhost:8080/api/dashboard/{username}/count-total
  @GetMapping("/{username}/count-total")
  public ResponseEntity<Long> countTotalNumberOfExpenses(@PathVariable String username) {
    long totalNumberOfExpenses = dashboardService.countTotalNumberOfExpenses(username);
    return new ResponseEntity<>(totalNumberOfExpenses, HttpStatus.OK);
  }

  // Calculate individual/group member balances
  // http://localhost:8080/api/dashboard/{username}/balances
  @GetMapping("/{username}/balances")
  public ResponseEntity<Map<String, Double>> calculateNetBalances(@PathVariable String username) {
    Map<String, Double> balances = dashboardService.calculateNetBalances(username);
    return new ResponseEntity<>(balances, HttpStatus.OK);
  }

  // Fetch all expenses details
  // http://localhost:8080/api/dashboard/{username}/expenses
  @GetMapping("/{username}/expenses")
  public ResponseEntity<List<Expense>> getAllIndividualExpenses(@PathVariable String username) {
    List<Expense> expenses = dashboardService.getAllIndividualExpenses(username);
    return new ResponseEntity<>(expenses, HttpStatus.OK);
  }

  // Reset dashboard
  // http://localhost:8080/api/dashboard/{username}/reset

  @DeleteMapping("/{username}/reset")
  public ResponseEntity<Void> resetDashboard(@PathVariable String username) {
    dashboardService.resetDashboard(username);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
