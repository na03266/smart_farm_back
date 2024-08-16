package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.SensorData;

import java.time.LocalDateTime;

public class SensorDataDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AddSensorDataRequest {
        private Integer sensorId;
        private Float sensorValue;
    }

    @Getter
    public static class SensorDataResponse {
        private final Long id;
        private final Integer sensorId;
        private final Float sensorValue;
        private final LocalDateTime recordedAt;
        private final String controllerId;


        public SensorDataResponse(SensorData sensorData) {
            this.id = sensorData.getId();
            this.sensorId = sensorData.getSensorId();
            this.sensorValue = sensorData.getSensorValue();
            this.controllerId = sensorData.getControllerId();
            this.recordedAt = sensorData.getRecordedAt();

        }
    }
    @Getter
    @AllArgsConstructor
    @Builder
    public static class SensorDataAverageResponse {
        private final Integer sensorId;
        private final Double averageValue;
        private final LocalDateTime timeBlock;
    }

    // 인터페이스 정의 (Repository에서 사용)
    public interface SensorDataAverage {
        Integer getSensorId();
        Double getAverageValue();
        LocalDateTime getTimeBlock();
    }
}