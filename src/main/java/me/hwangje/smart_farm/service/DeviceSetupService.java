package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.DeviceSetup;
import me.hwangje.smart_farm.dto.DeviceSetupDto.*;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.DeviceSetupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceSetupService {
    private final DeviceSetupRepository deviceSetupRepository;

    // Create
    @Transactional
    public DeviceSetup save(AddDeviceSetupRequest request, Controller controller) {
        DeviceSetup deviceSetup = DeviceSetup.builder()
                .unitId(request.getUnitId())
                .unitType(request.getUnitType())
                .unitCh(request.getUnitCh())
                .unitOpenCh(request.getUnitOpenCh())
                .unitCloseCh(request.getUnitCloseCh())
                .unitMoveTime(request.getUnitMoveTime())
                .unitStopTime(request.getUnitStopTime())
                .unitOpenTime(request.getUnitOpenTime())
                .unitCloseTime(request.getUnitCloseTime())
                .operationType(request.getOperationType())
                .timerSet(request.getTimerSet())
                .controller(controller)
                .build();
        return deviceSetupRepository.save(deviceSetup);
    }

    // Read (단일 DeviceSetup)
    public DeviceSetup findById(Long id) {
        return deviceSetupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("DeviceSetup not found with id: " + id));
    }

    // Read (모든 DeviceSetup)
    public List<DeviceSetup> findAllByControllerId(Long controllerId) {
        return deviceSetupRepository.findAllByController_Id(controllerId);
    }

    // Update
    @Transactional
    public DeviceSetup update(Long id, UpdateDeviceSetupRequest request) {
        DeviceSetup deviceSetup = findById(id);

        deviceSetup.update(
                request.getUnitId(),
                request.getUnitType(),
                request.getUnitCh(),
                request.getUnitOpenCh(),
                request.getUnitCloseCh(),
                request.getUnitMoveTime(),
                request.getUnitStopTime(),
                request.getUnitOpenTime(),
                request.getUnitCloseTime(),
                request.getOperationType(),
                request.getTimerSet()
        );
        return deviceSetup;
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        DeviceSetup deviceSetup = findById(id);
        deviceSetupRepository.delete(deviceSetup);
    }
}