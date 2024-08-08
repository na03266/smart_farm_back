package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE " +
            "(:nameLike IS NULL OR u.name LIKE %:nameLike%) AND " +
            "(:phoneLike IS NULL OR u.contact LIKE %:phoneLike%) AND " +
            "(:managerNameLike IS NULL OR u.manager LIKE %:managerNameLike%)")
    List<User> findByCriteria(
            @Param("nameLike") String nameLike,
            @Param("phoneLike") String phoneLike,
            @Param("managerNameLike") String managerNameLike
    );

    @Query("SELECT u FROM User u WHERE u.group = :group AND " +
            "(:nameLike IS NULL OR u.name LIKE %:nameLike%) AND " +
            "(:phoneLike IS NULL OR u.contact LIKE %:phoneLike%) AND " +
            "(:managerNameLike IS NULL OR u.manager LIKE %:managerNameLike%) AND " +
            "u.role <> me.hwangje.smart_farm.domain.Role.ADMIN")
    List<User> findByGroupAndCriteria(
            @Param("group") Group group,
            @Param("nameLike") String nameLike,
            @Param("phoneLike") String phoneLike,
            @Param("managerNameLike") String managerNameLike
    );
}
