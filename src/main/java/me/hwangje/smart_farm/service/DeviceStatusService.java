package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.DeviceStatus;
import me.hwangje.smart_farm.dto.DeviceStatusDto.AddDeviceStatusRequest;
import me.hwangje.smart_farm.dto.DeviceStatusDto.UpdateDeviceStatusRequest;
import me.hwangje.smart_farm.repository.DeviceStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceStatusService {
    private final DeviceStatusRepository deviceStatusRepository;

    // Create
    @Transactional
    public DeviceStatus save(AddDeviceStatusRequest request, Controller controller) {
        DeviceStatus deviceStatus = DeviceStatus.builder()
                .unitId(request.getUnitId())
                .isAutoMode(request.getIsAutoMode())
                .status(request.getStatus())
                .controller(controller)
                .build();
        return deviceStatusRepository.save(deviceStatus);
    }

    // Read (단일 DeviceStatus)
    public DeviceStatus findById(Long id) {
        return deviceStatusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DeviceStatus not found with id: " + id));
    }

    // Read (모든 DeviceStatus)
    public List<DeviceStatus> findAllByControllerId(Long controllerId) {
        return deviceStatusRepository.findAllByController_Id(controllerId);
    }

    // Update
    @Transactional
    public DeviceStatus update(Long id, UpdateDeviceStatusRequest request) {
        DeviceStatus deviceStatus = findById(id);

        deviceStatus.update(
                request.getUnitId(),
                request.getIsAutoMode(),
                request.getStatus()
        );
        return deviceStatus;
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        DeviceStatus deviceStatus = findById(id);
        deviceStatusRepository.delete(deviceStatus);
    }

    @Transactional
    public void deleteAllByController(Controller controller) {
        deviceStatusRepository.deleteAllByController(controller);
    }
}