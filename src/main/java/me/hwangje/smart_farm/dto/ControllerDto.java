package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;

public class ControllerDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AddControllerRequest {
        private String controllerId;
        private String name;
        private String setTempLow;
        private String setTempHigh;
        private Integer tempGap;
        private Integer heatTemp;
        private Integer iceType;
        private Integer alarmType;
        private Integer alarmTempHigh;
        private Integer alarmTempLow;
        private String tel;
        private Integer awsEnabled;
        private Long userId;
    }

    @Getter
    public static class ControllerResponse {
        private final String controllerId;
        private final String name;
        private final String setTempLow;
        private final String setTempHigh;
        private final Integer tempGap;
        private final Integer heatTemp;
        private final Integer iceType;
        private final Integer alarmType;
        private final Integer alarmTempHigh;
        private final Integer alarmTempLow;
        private final String tel;
        private final Integer awsEnabled;
        private final Long userId;

        public ControllerResponse(Controller controller) {
            this.controllerId = controller.getControllerId();
            this.name = controller.getName();
            this.setTempLow = controller.getSetTempLow();
            this.setTempHigh = controller.getSetTempHigh();
            this.tempGap = controller.getTempGap();
            this.heatTemp = controller.getHeatTemp();
            this.iceType = controller.getIceType();
            this.alarmType = controller.getAlarmType();
            this.alarmTempHigh = controller.getAlarmTempHigh();
            this.alarmTempLow = controller.getAlarmTempLow();
            this.tel = controller.getTel();
            this.awsEnabled = controller.getAwsEnabled();
            this.userId = controller.getUserId();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateControllerRequest {
        private String name;
        private String setTempLow;
        private String setTempHigh;
        private Integer tempGap;
        private Integer heatTemp;
        private Integer iceType;
        private Integer alarmType;
        private Integer alarmTempHigh;
        private Integer alarmTempLow;
        private String tel;
        private Integer awsEnabled;
        private Long userId;
    }
}
