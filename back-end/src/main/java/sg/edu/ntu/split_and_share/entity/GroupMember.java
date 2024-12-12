package sg.edu.ntu.split_and_share.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({ "expenses" }) // Ignore the "expenses" field during serialization
public class GroupMember {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "member_name")
  private String memberName;

  /*
   * I set an initial balance of 0.0 so the value will not be null to avoid
   * NullPointerException
   */
  @Column(name = "balance", nullable = false)
  private Double balance = 0.0;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "dashboard_id", nullable = false)
  private Dashboard dashboard;

  @ManyToMany(mappedBy = "sharedBy") // Bidirectional mapping
  @JsonBackReference
  private Set<Expense> expenses;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getMemberName() {
    return this.memberName;
  }

  public void setMemberName(String memberName) {
    this.memberName = memberName;
  }

  public Double getBalance() {
    return this.balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  public Dashboard getDashboard() {
    return this.dashboard;
  }

  public void setDashboard(Dashboard dashboard) {
    this.dashboard = dashboard;
  }

}
