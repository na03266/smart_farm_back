package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {
    List<Group> findByNameContaining(String name);
}
