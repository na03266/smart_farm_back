package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.SensorSetup;
import me.hwangje.smart_farm.dto.SensorSetupDto.*;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.SensorSetupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SensorSetupService {
    private final SensorSetupRepository sensorSetupRepository;
    private final ControllerRepository controllerRepository;

    // Create
    @Transactional
    public SensorSetup save(AddSensorSetupRequest request, Controller controller) {
        SensorSetup sensorSetup = SensorSetup.builder()
                .sensorId(request.getSensorId())
                .sensorCh(request.getSensorCh())
                .sensorReserved(request.getSensorReserved())
                .sensorMult(request.getSensorMult())
                .sensorOffset(request.getSensorOffset())
                .sensorFormula(request.getSensorFormula())
                .controller(controller)
                .build();
        return sensorSetupRepository.save(sensorSetup);
    }

    // Read (단일 SensorSetup)
    public SensorSetup findById(Long id) {
        return sensorSetupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SensorSetup not found with id: " + id));
    }

    // Read (모든 SensorSetup)
    public List<SensorSetup> findAllByControllerId(Long controllerId) {
        return sensorSetupRepository.findAllByController_Id(controllerId);
    }

    // Update
    @Transactional
    public SensorSetup update(Long id, UpdateSensorSetupRequest request) {
        SensorSetup sensorSetup = findById(id);

        sensorSetup.update(
                request.getSensorId(),
                request.getSensorCh(),
                request.getSensorReserved(),
                request.getSensorMult(),
                request.getSensorOffset(),
                request.getSensorFormula()
        );
        return sensorSetup;
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        SensorSetup sensorSetup = findById(id);
        sensorSetupRepository.delete(sensorSetup);
    }
}