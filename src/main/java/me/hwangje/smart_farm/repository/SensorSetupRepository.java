package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.SensorSetup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SensorSetupRepository  extends JpaRepository<SensorSetup, Long> {
    List<SensorSetup> findAllByController_Id(Long controllerId);

    void deleteAllByController(Controller controller);

    Optional<SensorSetup> findByControllerAndSensorId(Controller controller, int i);
}
