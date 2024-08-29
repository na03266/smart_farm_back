package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.DeviceSetup;
import me.hwangje.smart_farm.domain.DeviceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceStatusRepository extends JpaRepository<DeviceStatus, Long> {
    List<DeviceStatus> findAllByController_Id(Long controllerId);
    Optional<DeviceStatus> findByUnitIdAndControllerId(Integer unitId, String controllerId);

    void deleteAllByController(Controller controller);
}
