package ib.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ib.project.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByUsername(String username);

	@Query(value = "SELECT * FROM users AS u WHERE u.enabled = false", nativeQuery = true)
	List<User> getInactiveUsers();
}
