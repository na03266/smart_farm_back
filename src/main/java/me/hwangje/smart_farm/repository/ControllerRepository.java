package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ControllerRepository extends JpaRepository<Controller, Long> {
    Optional<Controller> findByControllerId(String controllerId);

    @Query("SELECT c FROM Controller c WHERE " +
            "(:controllerName is null OR c.name LIKE %:controllerName%) AND " +
            "(:userName is null OR c.user.name LIKE %:userName%) AND " +
            "(:groupName is null OR c.user.group.name LIKE %:groupName%)")
    List<Controller> searchControllers(
            @Param("controllerName") String controllerName,
            @Param("userName") String userName,
            @Param("groupName") String groupName
    );
}
