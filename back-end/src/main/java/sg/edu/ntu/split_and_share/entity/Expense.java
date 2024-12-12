package sg.edu.ntu.split_and_share.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "expense")
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "type")
  private String type;

  @Column(name = "amount")
  private Double amount;

  @Column(name = "description")
  private String description;

  @Column(name = "paid_by")
  private String paidBy;

  @ManyToOne(cascade = CascadeType.ALL)
  @JsonIgnore
  @JoinColumn(name = "dashboard_id", nullable = false)
  private Dashboard dashboard;

  @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
  @JsonIgnoreProperties("expense")
  @JoinTable(name = "expense_group_members", joinColumns = @JoinColumn(name = "expense_id"), inverseJoinColumns = @JoinColumn(name = "group_member_id"))
  private Set<GroupMember> sharedBy;
  /*
   * Set is used instead of List is because a Set does not allow duplicates
   * meaning user won't accidentally input same person twice in any expense, also
   * unlike List, there is no order required, which means user can enter names in
   * any order and it will work with no issue.
   */

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Double getAmount() {
    return this.amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPaidBy() {
    return this.paidBy;
  }

  public void setPaidBy(String paidBy) {
    this.paidBy = paidBy;
  }

  public Dashboard getDashboard() {
    return this.dashboard;
  }

  public void setDashboard(Dashboard dashboard) {
    this.dashboard = dashboard;
  }

  public Set<GroupMember> getSharedBy() {
    return this.sharedBy;
  }

  public void setSharedBy(Set<GroupMember> sharedBy) {
    this.sharedBy = sharedBy;
  }

}
