package sg.edu.ntu.split_and_share.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.ExpenseNotFoundException;
import sg.edu.ntu.split_and_share.exception.GroupMemberNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.ExpenseRepository;
import sg.edu.ntu.split_and_share.repository.GroupMemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

  private ExpenseRepository expenseRepository;
  private DashboardRepository dashboardRepository;
  private GroupMemberRepository groupMemberRepository;
  private EntityManager entityManager;
  private static final Logger logger = LoggerFactory.getLogger(ExpenseServiceImpl.class);

  public ExpenseServiceImpl(ExpenseRepository expenseRepository, DashboardRepository dashboardRepository,
      GroupMemberRepository groupMemberRepository, EntityManager entityManager) {
    this.expenseRepository = expenseRepository;
    this.dashboardRepository = dashboardRepository;
    this.groupMemberRepository = groupMemberRepository;
    this.entityManager = entityManager;
  }

  // Create new expense
  @Transactional
  @Override
  public Expense addExpense(Expense expense, String username) {
    logger.info("Attempting to add expense to the active dashboard");
    // Fetch the dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("No dashboard found in the database");
          return new DashboardNotFoundException();
        });

    // Validate that all sharedBy group members exist, now using only memberName
    if (expense.getSharedBy() != null && !expense.getSharedBy().isEmpty()) {
      Set<GroupMember> validatedMembers = expense.getSharedBy().stream()
          .map(groupMember -> {
            // Look up the GroupMember by memberName, assuming memberName is unique
            String memberName = groupMember.getMemberName();
            return groupMemberRepository.findByMemberName(memberName)
                .orElseThrow(() -> {
                  logger.error("Group member with name: {} does not exist", memberName);
                  return new GroupMemberNotFoundException();
                });
          })
          .collect(Collectors.toSet());
      expense.setSharedBy(validatedMembers);
    } else {
      logger.warn("The expense does not have any shared group members");
    }

    // Attach the expense to the dashboard
    expense.setDashboard(dashboard);

    // Calculate and update the balance for each group member
    BigDecimal sharedAmount = BigDecimal.valueOf(expense.getAmount())
        .divide(BigDecimal.valueOf(expense.getSharedBy().size()), 2, RoundingMode.HALF_UP);

    expense.getSharedBy().forEach(groupMember -> {
      // Add the new sharedAmount to the existing balance
      BigDecimal newBalance = BigDecimal.valueOf(groupMember.getBalance()).add(sharedAmount);
      groupMember.setBalance(newBalance.doubleValue());

      // Save the updated balance to DB
      groupMemberRepository.save(groupMember);

    });

    // Save the expense
    Expense savedExpense = expenseRepository.save(expense);

    // Log the success
    logger.info("Successfully added expense with ID: {}", savedExpense.getId());

    return savedExpense;
  }

  // Get an expense
  @Override
  public Expense getExpense(Long id) {
    // Fetch and return the expense
    return expenseRepository.findById(id)
        .orElseThrow(() -> {
          logger.error("No expense found in the database with ID: {}", id);
          return new ExpenseNotFoundException();
        });
  }

  // Get all expenses
  @Override
  public List<Expense> getAllExpenses() {
    List<Expense> expenses = expenseRepository.findAll();
    logger.info("Fetched {} expenses from the database", expenses.size());
    return expenses;
  }

  // Edit expense
  @Transactional
  @Override
  public Expense updateExpense(Long id, Expense newDetails) {
    // Fetch the existing expense
    Expense existingExpense = expenseRepository.findById(id)
        .orElseThrow(() -> {
          logger.error("No expense found in the database with ID: {}", id);
          return new ExpenseNotFoundException();
        });

    // Validate new details (exclude sharedBy as it can be an expense with only 1
    // member which means sharedBy could be null)
    if (newDetails.getType() == null || newDetails.getAmount() == null || newDetails.getPaidBy() == null) {
      logger.error("Invalid expense details provided for update");
      throw new IllegalArgumentException("Expense details cannot be null");
    }

    // Step 1: Remove the old shared amounts and update balances directly here
    removeOldSharedAmounts(existingExpense);

    // Step 2: Update changes in "type" and "description"
    existingExpense.setType(newDetails.getType());
    existingExpense.setDescription(newDetails.getDescription());

    // Step 3: Update new "amount" and "sharedBy" if they have changed
    if (!newDetails.getAmount().equals(existingExpense.getAmount()) ||
        !newDetails.getSharedBy().equals(existingExpense.getSharedBy())) {

      existingExpense.setAmount(newDetails.getAmount());
      Set<GroupMember> newSharedBy = new HashSet<>();

      // Validate and update sharedBy members
      if (newDetails.getSharedBy() != null && !newDetails.getSharedBy().isEmpty()) {
        for (GroupMember member : newDetails.getSharedBy()) {
          Optional<GroupMember> validMember = groupMemberRepository.findByDashboard_IdAndMemberName(
              existingExpense.getDashboard().getId(), member.getMemberName());

          if (!validMember.isPresent()) {
            logger.error("Member {} does not belong to the same dashboard", member.getMemberName());
            throw new IllegalArgumentException("GroupMember does not belong to the same dashboard");
          }

          newSharedBy.add(validMember.get());
        }
      }

      existingExpense.setSharedBy(newSharedBy);

      // Step 4: Redistribute the new amount among the new sharedBy members
      redistributeBalances(existingExpense, newDetails.getAmount());
    }

    // Handle "paidBy" changes
    existingExpense.setPaidBy(newDetails.getPaidBy());

    // Save updated expense
    logger.info("Successfully updated expense with ID: {}", id);
    return expenseRepository.save(existingExpense);
  }

  // Helper method to remove shared amount from group members from original
  // expense before update / delete expense
  // this helper method is used is both updateExpense() and deleteExpense()
  private void removeOldSharedAmounts(Expense expense) {
    double originalAmount = expense.getAmount();
    Set<GroupMember> originalSharedBy = new HashSet<>(expense.getSharedBy());

    if (originalSharedBy != null && !originalSharedBy.isEmpty()) {
      BigDecimal originalSharePerMember = BigDecimal.valueOf(originalAmount)
          .divide(BigDecimal.valueOf(originalSharedBy.size()), 2, RoundingMode.HALF_UP);
      logger.info("Original expense amount: {}, shared by: {}", originalAmount, originalSharedBy);
      logger.info("Original share per member: {}", originalSharePerMember);

      for (GroupMember member : originalSharedBy) {
        BigDecimal previousBalance = BigDecimal.valueOf(member.getBalance());
        BigDecimal updatedBalance = previousBalance.subtract(originalSharePerMember);

        logger.debug("Subtracting {} from member '{}', previous balance: {}, new balance: {}",
            originalSharePerMember, member.getMemberName(), previousBalance, updatedBalance);

        member.setBalance(updatedBalance.doubleValue());
        groupMemberRepository.save(member);

        logger.debug("After save, member '{}', balance: {}", member.getMemberName(), member.getBalance());
      }
    }
  }

  // Helper method to distribute updated expense to group members involved in
  // updated expense
  private void redistributeBalances(Expense expense, Double newAmount) {
    logger.info("Starting balance redistribution for expense ID: {}", expense.getId());

    if (newAmount != null) {
      Set<GroupMember> newSharedBy = expense.getSharedBy();
      logger.info("New expense amount: {}, shared by: {}", newAmount, newSharedBy);

      if (newSharedBy != null && !newSharedBy.isEmpty()) {
        BigDecimal newSharePerMember = BigDecimal.valueOf(newAmount).divide(BigDecimal.valueOf(newSharedBy.size()), 2,
            RoundingMode.HALF_UP);
        logger.info("New share per member: {}", newSharePerMember);

        for (GroupMember member : newSharedBy) {
          BigDecimal previousBalance = BigDecimal.valueOf(member.getBalance());
          BigDecimal updatedBalance = previousBalance.add(newSharePerMember);

          logger.debug("Adding {} to member '{}', previous balance: {}, new balance: {}",
              newSharePerMember, member.getMemberName(), previousBalance, updatedBalance);

          member.setBalance(updatedBalance.doubleValue()); // Convert BigDecimal to Double
          groupMemberRepository.save(member);

          logger.debug("After save, member '{}', balance: {}", member.getMemberName(), member.getBalance());
        }
      }
    }

    expense.setAmount(newAmount != null ? newAmount : expense.getAmount());
    groupMemberRepository.flush();
    logger.info("Balance redistribution completed for expense ID: {}", expense.getId());
  }

  // Delete expense
  @Transactional
  @Override
  public void deleteExpense(Long id) {
    // Ensure the expense exists before deletion
    Expense expense = entityManager.find(Expense.class, id);
    if (expense == null) {
      logger.error("No expense found in the database with ID: {}", id);
      throw new ExpenseNotFoundException();
    }

    logger.info("Attempting to delete expense with ID: {}", id);

    // Subtract the shared amounts from the balances of the members
    removeOldSharedAmounts(expense);

    // Clear associations (it detaches all group members from this expense)
    expense.getSharedBy().clear();
    expense.setDashboard(null);

    // Delete the expense
    entityManager.remove(expense);
    entityManager.flush();

    logger.info("Successfully deleted expense with ID: {}", id);
  }

}
