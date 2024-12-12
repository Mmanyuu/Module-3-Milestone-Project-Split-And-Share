package sg.edu.ntu.split_and_share.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.ntu.split_and_share.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

  // Find members from the dashboard by dashboard ID and memberName
  Optional<GroupMember> findByDashboard_IdAndMemberName(Long dashboardId, String memberName);

  // Find all members that belong to a given dashboard ID
  List<GroupMember> findByDashboard_Id(Long dashboardId);

  // Custom method to find a GroupMember by their memberName
  // used in addExpense()
  Optional<GroupMember> findByMemberName(String memberName);

}
