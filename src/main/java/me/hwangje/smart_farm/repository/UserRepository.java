package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    List<User> findByUserNameContaining(String nameFragment);
    List<User> findByRole(String role);
    List<User> findByPhoneNumber(String phoneNumber);
    List<User> findByEmailContainingOrUserNameContaining(String email, String userName);

}
