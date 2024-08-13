package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.DeviceSetup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceSetupRepository extends JpaRepository<DeviceSetup, Long> {
    List<DeviceSetup> findAllByController_Id(Long controllerId);
}
