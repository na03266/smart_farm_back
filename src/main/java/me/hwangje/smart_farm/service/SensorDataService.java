package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.SensorData;
import me.hwangje.smart_farm.dto.SensorDataDto.*;
import me.hwangje.smart_farm.repository.SensorDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorDataService {
    private final SensorDataRepository sensorDataRepository;

    // Create
    @Transactional
    public SensorData save(AddSensorDataRequest request, Controller controller) {
        SensorData sensorData = SensorData.builder()
                .sensorId(request.getSensorId())
                .sensorValue(request.getSensorValue())
                .controller(controller)
                .build();
        return sensorDataRepository.save(sensorData);
    }

    // Read (단일 SensorData)
    public SensorData findById(Long id) {
        return sensorDataRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SensorData not found with id: " + id));
    }

    //Read (리스트 현재)
    public List<SensorData> findLatestByControllerId(Long id) {
        return sensorDataRepository.findLatestByControllerId(id);
    }

    // 30분단위 평균 데이터
    public List<SensorDataAverageResponse> findAveragesByControllerIdAndTimeRange(Long controllerId, LocalDateTime startTime, LocalDateTime endTime) {
        List<SensorDataAverage> averages = sensorDataRepository.findAveragesByControllerIdAndTimeRange(controllerId, startTime, endTime);

        return averages.stream()
                .map(avg -> SensorDataAverageResponse.builder()
                        .sensorId(avg.getSensorId())
                        .averageValue(avg.getAverageValue())
                        .timeBlock(avg.getTimeBlock())
                        .build())
                .collect(Collectors.toList());
    }

    //일일 평균 데이터
    public List<SensorDataAverageResponse> findDailyAveragesByControllerIdAndDateRange(Long controllerId, LocalDate startDate, LocalDate endDate) {
        List<SensorDataAverage> averages = sensorDataRepository.findDailyAveragesByControllerIdAndDateRange(controllerId, startDate, endDate);

        return averages.stream()
                .map(avg -> new SensorDataAverageResponse(avg.getSensorId(), avg.getAverageValue(), avg.getTimeBlock()))
                .collect(Collectors.toList());
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        SensorData sensorData = findById(id);
        sensorDataRepository.delete(sensorData);
    }

    @Transactional
    public void deleteAllByController(Controller controller) {
        sensorDataRepository.deleteAllByController(controller);
    }
}