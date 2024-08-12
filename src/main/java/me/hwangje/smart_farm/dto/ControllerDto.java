package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.User;

public class ControllerDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class AddControllerRequest {
        private String controllerId;
        private Float setTempLow;
        private Float setTempHigh;
        private Float tempGap;
        private Float heatTemp;
        private Integer iceType;
        private Integer alarmType;
        private Float alarmTempHigh;
        private Float alarmTempLow;
        private String tel;
        private Boolean awsEnabled;
        private Group group;
        private User user;

        public Controller toEntity() {
            return Controller.builder()
                    .controllerId(controllerId)
                    .setTempLow(setTempLow)
                    .setTempHigh(setTempHigh)
                    .tempGap(tempGap)
                    .heatTemp(heatTemp)
                    .iceType(iceType)
                    .alarmType(alarmType)
                    .alarmTempHigh(alarmTempHigh)
                    .alarmTempLow(alarmTempLow)
                    .tel(tel)
                    .awsEnabled(awsEnabled)
                    .group(group)
                    .user(user)
                    .build();
        }
    }

    @Getter
    public static class ControllerResponse {
        private String controllerId;
        private Float setTempLow;
        private Float setTempHigh;
        private Float tempGap;
        private Float heatTemp;
        private Integer iceType;
        private Integer alarmType;
        private Float alarmTempHigh;
        private Float alarmTempLow;
        private String tel;
        private Boolean awsEnabled;
        private Group group;
        private User user;

        public ControllerResponse(Controller controller) {
            this.controllerId = controller.getControllerId();
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
            this.group = controller.getGroup();
            this.user = controller.getUser();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateControllerRequest {
        private String name;
        private Float setTempLow;
        private Float setTempHigh;
        private Float tempGap;
        private Float heatTemp;
        private Integer iceType;
        private Integer alarmType;
        private Float alarmTempHigh;
        private Float alarmTempLow;
        private String tel;
        private Boolean awsEnabled;
        private Group group;
        private User user;
    }
}
