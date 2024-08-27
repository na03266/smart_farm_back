package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.DeviceSetup;

public class DeviceSetupDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AddDeviceSetupRequest {
        private Integer unitId;
        private Integer unitType;
        private Integer unitCh;
        private Integer unitOpenCh;
        private Integer unitCloseCh;
        private Integer unitMoveTime;
        private Integer unitStopTime;
        private Integer unitOpenTime;
        private Integer unitCloseTime;
        private Integer operationType;
        private Integer timerSet;
    }

    @Getter
    public static class DeviceSetupResponse {
        private final Long id;
        private final Integer unitId;
        private final Integer unitType;
        private final Integer unitCh;
        private final Integer unitOpenCh;
        private final Integer unitCloseCh;
        private final Integer unitMoveTime;
        private final Integer unitStopTime;
        private final Integer unitOpenTime;
        private final Integer unitCloseTime;
        private final Integer operationType;
        private final Integer timerSet;

        public DeviceSetupResponse(DeviceSetup deviceSetup) {
            this.id = deviceSetup.getId();
            this.unitId = deviceSetup.getUnitId();
            this.unitType = deviceSetup.getUnitType();
            this.unitCh = deviceSetup.getUnitCh();
            this.unitOpenCh = deviceSetup.getUnitOpenCh();
            this.unitCloseCh = deviceSetup.getUnitCloseCh();
            this.unitMoveTime = deviceSetup.getUnitMoveTime();
            this.unitStopTime = deviceSetup.getUnitStopTime();
            this.unitOpenTime = deviceSetup.getUnitOpenTime();
            this.unitCloseTime = deviceSetup.getUnitCloseTime();
            this.operationType = deviceSetup.getOperationType();
            this.timerSet = deviceSetup.getTimerSet();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateDeviceSetupRequest {
        private Integer unitId;
        private Integer unitType;
        private Integer unitCh;
        private Integer unitOpenCh;
        private Integer unitCloseCh;
        private Integer unitMoveTime;
        private Integer unitStopTime;
        private Integer unitOpenTime;
        private Integer unitCloseTime;
        private Integer operationType;
        private Integer timerSet;
    }
}