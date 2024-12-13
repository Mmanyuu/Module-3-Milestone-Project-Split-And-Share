package sg.edu.ntu.split_and_share.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "dashboard")
public class Dashboard {
  // @Id
  // private String username;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  // @MapsId
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnoreProperties("dashboard")
  private User user;

  @Column(name = "name", nullable = false)
  private String name;

  // "orphanRemoval = true" makes sure when expense table is removed, all related
  // entities are deleted as well, this is essential in order to properly execute
  // resetDashboard()
  @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<Expense> expenses;

  @OneToMany(mappedBy = "dashboard", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  private List<GroupMember> groupMembers;

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Expense> getExpenses() {
    return this.expenses;
  }

  public void setExpenses(List<Expense> expenses) {
    this.expenses = expenses;
  }

  public List<GroupMember> getGroupMembers() {
    return this.groupMembers;
  }

  public void setGroupMembers(List<GroupMember> groupMembers) {
    this.groupMembers = groupMembers;
  }

}