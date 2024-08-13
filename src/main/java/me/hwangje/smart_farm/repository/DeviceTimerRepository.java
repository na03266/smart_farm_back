package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.DeviceTimer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceTimerRepository extends JpaRepository<DeviceTimer, Long> {

    List<DeviceTimer> findAllByController_Id(Long controllerId);
}
