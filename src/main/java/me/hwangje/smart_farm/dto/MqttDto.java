package me.hwangje.smart_farm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class MqttDto {

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class UpdateSetupRequest {
        /*
          SetupData
         */
        private String controllerId; // 컨트롤러 맥주소
        private String settempL; // 온도 설정(24시간-30분간격)
        private String settempH; // 온도 설정(24시간-30분간격)
        private int TEMPGAP; // 냉동기 및 제상히터 온도 편차
        private int HEATTEMP; // 제상히터 온도 설정
        private int ICETYPE; // 에어컨 또는 냉동기의 타입 정의 (냉동기 0, 에어컨 1 ~ 3)
        private int ALARMTYPE; // 알람 모드 정의
        private int ALARMTEMPH; // 최고온도 알람설정
        private int ALARMTEMPL; // 최저온도 알람설정
        private String TEL; // SMS 서비스 전화번호 설정
        private int AWSBIT; // AWS 클라우드 사용여부
        private List<String> UTIMER; // 장치 시간 예약 설정
        private List<UpdateSetupDevice> setDevice;
        private List<UpdateSetupSensor> setSensor;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class UpdateSetupSensor {
        private int SID;
        private int SCH;
        private int SRESERVED;
        private double SMULT;
        private double SOFFSET;
        private String SEQ;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class UpdateSetupDevice {
        private int UID;
        private int UTYPE;
        private int UCH;
        private int UOPENCH;
        private int UCLOSECH;
        private int UMVTIME;
        private int USTTIME;
        private int UOPENTIME;
        private int UCLOSETIME;
        private int UOPTYPE;
        private int UTIMERSET;
    }

//    @NoArgsConstructor
//    @AllArgsConstructor
//    @Getter
//    public static class UpdateDeviceStatusRequest {
//        /*
//         * CID, List<DeviceStatus>
//         */
//    }

}
