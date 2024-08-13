package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.DeviceTimer;

public class DeviceTimerDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AddDeviceTimerRequest {
        private Integer timerId;
        private String timer;
        private String name;
    }

    @Getter
    public static class DeviceTimerResponse {
        private final Long id;
        private final Integer timerId;
        private final String timer;
        private final String name;

        public DeviceTimerResponse(DeviceTimer deviceTimer) {
            this.id = deviceTimer.getId();
            this.timerId = deviceTimer.getTimerId();
            this.timer = deviceTimer.getTimer();
            this.name = deviceTimer.getName();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateDeviceTimerRequest {
        private Integer timerId;
        private String timer;
        private String name;
    }
}