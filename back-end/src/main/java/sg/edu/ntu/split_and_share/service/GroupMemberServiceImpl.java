package sg.edu.ntu.split_and_share.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import sg.edu.ntu.split_and_share.entity.Dashboard;
import sg.edu.ntu.split_and_share.entity.GroupMember;
import sg.edu.ntu.split_and_share.exception.DashboardNotFoundException;
import sg.edu.ntu.split_and_share.exception.GroupMemberNotFoundException;
import sg.edu.ntu.split_and_share.exception.UserNotFoundException;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.GroupMemberRepository;

import jakarta.transaction.Transactional;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

  private GroupMemberRepository groupMemberRepository;
  private DashboardRepository dashboardRepository;

  private static final Logger logger = LoggerFactory.getLogger(GroupMemberServiceImpl.class);

  public GroupMemberServiceImpl(GroupMemberRepository groupMemberRepository, DashboardRepository dashboardRepository) {
    this.groupMemberRepository = groupMemberRepository;
    this.dashboardRepository = dashboardRepository;
  }

  // Create group member(s)
  @Transactional
  @Override
  public List<String> addGroupMembers(List<String> groupMemberList, String username) {
    logger.info("Attempting to add group members to the active dashboard");

    // Validate existence of a dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("No dashboard found in the database");
          return new DashboardNotFoundException();
        });
    logger.info("Fetched dashboard for username: {}", username);

    // Save each group member using a for loop
    for (String groupMember : groupMemberList) {
      GroupMember member = new GroupMember();
      member.setMemberName(groupMember);
      member.setBalance(0.0);
      member.setDashboard(dashboard);

      groupMemberRepository.save(member);
      logger.info("Added member '{}' successfully to the dashboard '{}'", groupMember, dashboard.getName());
    }
    logger.info("Successfully added all group members to the dashboard for username: {}", username);
    return groupMemberList;
  }

  // Delete group member
  @Override
  public void removeGroupMember(String memberName, String username) {
    logger.info("Attempting to remove group member '{}'", memberName);

    // Validate existence of a dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("No dashboard found in the database");
          return new UserNotFoundException();
        });

    // Find the group member in the dashboard
    GroupMember groupMember = groupMemberRepository.findByDashboard_IdAndMemberName(
        dashboard.getId(), memberName).orElseThrow(() -> {
          logger.error("Group member '{}' not found in the dashboard", memberName);
          return new GroupMemberNotFoundException();
        });

    // Check if group member is tied to any expenses
    if (!groupMember.getDashboard().getExpenses().isEmpty()) {
      logger.error("Cannot remove member '{}' because they are tied to existing expenses", memberName);
      throw new IllegalStateException("Cannot remove member as they are tied to existing expenses.");
    }

    // If validation passes, delete the member
    groupMemberRepository.delete(groupMember);
    logger.info("Group member '{}' removed successfully from the dashboard '{}'", memberName, dashboard.getName());
  }

  // List all group members
  @Override
  public List<String> getAllGroupMembers(String username) {
    logger.info("Fetching all group members");

    // Validate existence of a dashboard
    Dashboard dashboard = dashboardRepository.findByUser_Username(username)
        .orElseThrow(() -> {
          logger.error("No dashboard found in the database");
          return new UserNotFoundException();
        });

    List<GroupMember> members = groupMemberRepository.findByDashboard_Id(dashboard.getId());
    logger.info("Found {} members in the dashboard", members.size());

    return members.stream().map(GroupMember::getMemberName).collect(Collectors.toList());
    /*
     * Syntax explanation - the stream puts the members that fetched from dashboard
     * into the pipeline, for every group member in the stream, map() extracts the
     * memberName and then Collector gathers the result and present it as a list.
     */
  }
}
