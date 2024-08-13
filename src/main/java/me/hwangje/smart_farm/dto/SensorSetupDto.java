package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.SensorSetup;

public class SensorSetupDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AddSensorSetupRequest {
        private Integer sensorId;
        private Integer sensorCh;
        private Integer sensorReserved;
        private Float sensorMult;
        private Float sensorOffset;
        private String sensorFormula;
    }

    @Getter
    public static class SensorSetupResponse {
        private final Long id;
        private final Integer sensorId;
        private final Integer sensorCh;
        private final Integer sensorReserved;
        private final Float sensorMult;
        private final Float sensorOffset;
        private final String sensorFormula;

        public SensorSetupResponse(SensorSetup sensorSetup) {
            this.id = sensorSetup.getId();
            this.sensorId = sensorSetup.getSensorId();
            this.sensorCh = sensorSetup.getSensorCh();
            this.sensorReserved = sensorSetup.getSensorReserved();
            this.sensorMult = sensorSetup.getSensorMult();
            this.sensorOffset = sensorSetup.getSensorOffset();
            this.sensorFormula = sensorSetup.getSensorFormula();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateSensorSetupRequest {
        private Integer sensorId;
        private Integer sensorCh;
        private Integer sensorReserved;
        private Float sensorMult;
        private Float sensorOffset;
        private String sensorFormula;
    }
}