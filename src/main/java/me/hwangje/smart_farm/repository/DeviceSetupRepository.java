package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.DeviceSetup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceSetupRepository extends JpaRepository<DeviceSetup, Long> {
    List<DeviceSetup> findAllByController_Id(Long controllerId);

    void deleteAllByController(Controller controller);

    Optional<DeviceSetup> findByControllerAndUnitId(Controller controller, int i);
}
