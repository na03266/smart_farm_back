package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ControllerRepository extends JpaRepository<Controller, Long> {
    Optional<Controller> findByControllerId(String controllerId);
    List<Controller> findByUser(User user);
    List<Controller> findByGroup(Group group);

    List<Controller> findByNameContainingAndUser_NameContainingAndGroup_NameContaining(
            String controllerName, String userName, String groupName);
}
