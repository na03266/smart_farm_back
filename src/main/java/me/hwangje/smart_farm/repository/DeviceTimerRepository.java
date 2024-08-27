package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.DeviceTimer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceTimerRepository extends JpaRepository<DeviceTimer, Long> {

    List<DeviceTimer> findAllByController_Id(Long controllerId);

    void deleteAllByController(Controller controller);

    Optional<DeviceTimer> findByControllerAndTimerId(Controller controller, int i);
}
