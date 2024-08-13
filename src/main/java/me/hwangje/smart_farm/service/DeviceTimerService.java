package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.DeviceTimer;
import me.hwangje.smart_farm.dto.DeviceTimerDto.AddDeviceTimerRequest;
import me.hwangje.smart_farm.dto.DeviceTimerDto.UpdateDeviceTimerRequest;
import me.hwangje.smart_farm.repository.DeviceTimerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceTimerService {
    private final DeviceTimerRepository deviceTimerRepository;

    // Create
    @Transactional
    public DeviceTimer save(AddDeviceTimerRequest request, Controller controller) {
        DeviceTimer deviceTimer = DeviceTimer.builder()
                .timerId(request.getTimerId())
                .timer(request.getTimer())
                .name(request.getName())
                .controller(controller)
                .build();
        return deviceTimerRepository.save(deviceTimer);
    }

    // Read (단일 DeviceTimer)
    public DeviceTimer findById(Long id) {
        return deviceTimerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DeviceTimer not found with id: " + id));
    }

    // Read (모든 DeviceTimer)
    public List<DeviceTimer> findAllByControllerId(Long controllerId) {
            return deviceTimerRepository.findAllByController_Id(controllerId);
    }

    // Update
    @Transactional
    public DeviceTimer update(Long id, UpdateDeviceTimerRequest request) {
        DeviceTimer deviceTimer = findById(id);

        deviceTimer.update(
                request.getTimerId() != null ? request.getTimerId() : deviceTimer.getTimerId(),
                request.getTimer() != null ? request.getTimer() : deviceTimer.getTimer(),
                request.getName() != null ? request.getName() : deviceTimer.getName()
        );
        return deviceTimer;
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        DeviceTimer deviceTimer = findById(id);
        deviceTimerRepository.delete(deviceTimer);
    }
}