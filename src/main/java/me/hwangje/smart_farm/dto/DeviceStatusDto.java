package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.DeviceStatus;

public class DeviceStatusDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AddDeviceStatusRequest {
        private Integer unitId;
        private Boolean isAutoMode;
        private Integer status;
    }

    @Getter
    public static class DeviceStatusResponse {
        private final Long id;
        private final Integer unitId;
        private final Boolean isAutoMode;
        private final Integer status;
        private final String controllerId;

        public DeviceStatusResponse(DeviceStatus deviceStatus) {
            this.id = deviceStatus.getId();
            this.unitId = deviceStatus.getUnitId();
            this.isAutoMode = deviceStatus.getIsAutoMode();
            this.status = deviceStatus.getStatus();
            this.controllerId = deviceStatus.getControllerId();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateDeviceStatusRequest {
        private Integer unitId;
        private Boolean isAutoMode;
        private Integer status;
    }
}