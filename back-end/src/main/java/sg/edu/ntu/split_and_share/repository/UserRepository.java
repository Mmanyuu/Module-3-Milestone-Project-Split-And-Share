package sg.edu.ntu.split_and_share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.ntu.split_and_share.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Optional<User> findByUsernameAndPassword(String username, String password);
}
