package me.hwangje.smart_farm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.DeviceSetup;
import me.hwangje.smart_farm.domain.DeviceTimer;
import me.hwangje.smart_farm.domain.SensorSetup;
import me.hwangje.smart_farm.dto.MqttDto.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MqttPublishService {
    //보낼때
    // 갱신된 데이터를 반영해서 보낸다.
    // 조건별로 어떻게? 타이머, 센서, 유닛,
    // 그럼 그냥 모든 컨트롤러에다 가장 최근의 데이터를 받아서 mqtt로 전송하는 로직을 만든다면?\
    // 컨트롤러의 업데이트 쿼리가 끝난 뒤 해당
    // 잠깐. CID가 토픽에 포함되지 않는다면? 모든 유닛들이 모든 토픽을 리스닝 하고 있을텐데?
    // 그럼 앞에 CID가 들어간걸로 바꿔야겠네
    private final MqttService mqttService;
    private final ObjectMapper objectMapper;
    private final ControllerService controllerService;
    private final DeviceSetupService deviceSetupService;
    private final SensorSetupService sensorSetupService;
    private final DeviceTimerService deviceTimerService;


    public void publishSetup(Long id) throws JsonProcessingException {
        // 발행만 한다. 하면 JsonNode만 있으면 됨. 그럼 이걸 함수에서 만들어야함.
        Controller controller = controllerService.findById(id);
        JsonNode setupNode = convertSetupRequestToJsonNode(id);
        String payload = objectMapper.writeValueAsString(setupNode);

        mqttService.publishMessage("SMARTFARM/" + controller.getControllerId() + "/SETUP", payload);
    }

    private JsonNode convertSetupRequestToJsonNode(Long id) throws JsonProcessingException {
        // 컨트롤러 아이디만 가져다가 기본 세팅하고 하위 노드들은 여기에서 전부 처리해야함.
        //그럼 이 함수는 최종으로 만드는 것이니 일단 id만 받아서 컨트롤러 검색하고 dto제작이 아니라
        // dto를 맵핑만 해야함.
        UpdateSetupRequest setupRequest = mergedUpdateSetupDto(id);

        ObjectNode rootNode = objectMapper.createObjectNode();
        rootNode.set("CID", objectMapper.readTree(setupRequest.getControllerId()));
        rootNode.set("settempL", objectMapper.readTree(setupRequest.getSettempL()));
        rootNode.set("settempH", objectMapper.readTree(setupRequest.getSettempH()));
        rootNode.put("TEMPGAP", setupRequest.getTEMPGAP());
        rootNode.put("HEATTEMP", setupRequest.getHEATTEMP());
        rootNode.put("ICETYPE", setupRequest.getICETYPE());
        rootNode.put("ALARMTYPE", setupRequest.getALARMTYPE());
        rootNode.put("ALARMTEMPH", setupRequest.getALARMTEMPH());
        rootNode.put("ALARMTEMPL", setupRequest.getALARMTEMPL());
        rootNode.set("TEL", objectMapper.readTree(setupRequest.getTEL()));
        rootNode.put("AWSBIT", setupRequest.getAWSBIT());

        rootNode.set("setdevice", convertDeviceSetup(setupRequest.getSetDevice()));
        rootNode.set("UTIMER", convertDeviceTimerSetup(setupRequest.getUTIMER()));
        rootNode.set("setsensor", convertSensorSetup(setupRequest.getSetSensor()));

        return rootNode;
    }

    private ArrayNode convertDeviceSetup(List<UpdateSetupDevice> devices) {
        ArrayNode deviceArray = objectMapper.createArrayNode();

        for (UpdateSetupDevice device : devices) {
            ObjectNode deviceNode = objectMapper.createObjectNode();

            deviceNode.put("UID", device.getUID());
            deviceNode.put("UTYPE", device.getUTYPE());
            deviceNode.put("UCH", device.getUCH());
            deviceNode.put("UOPENCH", device.getUOPENCH());
            deviceNode.put("UCLOSECH", device.getUCLOSECH());
            deviceNode.put("UMVTIME", device.getUMVTIME());
            deviceNode.put("USTTIME", device.getUSTTIME());
            deviceNode.put("UOPENTIME", device.getUOPENTIME());
            deviceNode.put("UCLOSETIME", device.getUCLOSETIME());
            deviceNode.put("UOPTYPE", device.getUOPTYPE());
            deviceNode.put("UTIMERSET", device.getUTIMERSET());

            deviceArray.add(deviceNode);
        }
        return deviceArray;
    }

    private ArrayNode convertSensorSetup(List<UpdateSetupSensor> sensors) throws JsonProcessingException {
        ArrayNode sensorArray = objectMapper.createArrayNode();

        for (UpdateSetupSensor sensor : sensors) {
            ObjectNode sensorNode = objectMapper.createObjectNode();

            sensorNode.put("SID", sensor.getSID());
            sensorNode.put("SCH", sensor.getSCH());
            sensorNode.put("SRESERVED", sensor.getSRESERVED());
            sensorNode.put("SMULT", sensor.getSMULT());
            sensorNode.put("SOFFSET", sensor.getSOFFSET());
            sensorNode.set("SEQ", objectMapper.readTree(sensor.getSEQ()));

            sensorArray.add(sensorNode);
        }
        return sensorArray;
    }

    private ArrayNode convertDeviceTimerSetup(List<String> timers) {
        ArrayNode timerArray = objectMapper.createArrayNode();

        for (String timer : timers) {
            timerArray.add(timer);
        }
        return timerArray;
    }

    private UpdateSetupRequest mergedUpdateSetupDto(Long id) {
        Controller controller = controllerService.findById(id);

        return UpdateSetupRequest.builder()
                .controllerId(controller.getControllerId())
                .settempL(controller.getSetTempLow())
                .settempH(controller.getSetTempHigh())
                .TEMPGAP(controller.getTempGap())
                .HEATTEMP(controller.getHeatTemp())
                .ICETYPE(controller.getIceType())
                .ALARMTYPE(controller.getAlarmType())
                .ALARMTEMPH(controller.getAlarmTempHigh())
                .ALARMTEMPL(controller.getAlarmTempLow())
                .TEL(controller.getTel())
                .AWSBIT(controller.getAwsEnabled())
                .setSensor(createUpdateSetupSensor(id))
                .setDevice(createUpdateSetupDevice(id))
                .UTIMER(createUpdateSetupDeviceTimer(id))
                .build();
    }

    private List<UpdateSetupSensor> createUpdateSetupSensor(Long id) {
        List<SensorSetup> sensorSetups = sensorSetupService.findAllByControllerId(id);
        List<UpdateSetupSensor> updateSetupSensors = new ArrayList<>();

        for (SensorSetup sensor : sensorSetups) {
            UpdateSetupSensor updateSetupSensor = UpdateSetupSensor.builder()
                    .SID(sensor.getSensorId())
                    .SCH(sensor.getSensorCh())
                    .SRESERVED(sensor.getSensorReserved())
                    .SMULT(sensor.getSensorMult())
                    .SOFFSET(sensor.getSensorOffset())
                    .SEQ(sensor.getSensorFormula())
                    .build();
            updateSetupSensors.add(updateSetupSensor);
        }

        return updateSetupSensors;
    }

    private List<UpdateSetupDevice> createUpdateSetupDevice(Long id) {
        List<DeviceSetup> deviceSetups = deviceSetupService.findAllByControllerId(id);
        List<UpdateSetupDevice> updateSetupDevices = new ArrayList<>();

        for (DeviceSetup device : deviceSetups) {
            UpdateSetupDevice updateSetupSensor = UpdateSetupDevice.builder()
                    .UID(device.getUnitId())
                    .UTYPE(device.getUnitType())
                    .UCH(device.getUnitCh())
                    .UOPENCH(device.getUnitOpenCh())
                    .UCLOSECH(device.getUnitCloseCh())
                    .UMVTIME(device.getUnitMoveTime())
                    .USTTIME(device.getUnitStopTime())
                    .UOPENTIME(device.getUnitOpenTime())
                    .UCLOSETIME(device.getUnitCloseTime())
                    .UOPTYPE(device.getOperationType())
                    .UTIMERSET(device.getTimerSet())
                    .build();
            updateSetupDevices.add(updateSetupSensor);
        }

        return updateSetupDevices;
    }

    private List<String> createUpdateSetupDeviceTimer(Long id) {
        List<DeviceTimer> deviceTimers = deviceTimerService.findAllByControllerId(id);
        List<String> updateSetupDevicesTimers = new ArrayList<>();

        for (DeviceTimer timer : deviceTimers) {
            updateSetupDevicesTimers.add(timer.getTimer());
        }

        return updateSetupDevicesTimers;
    }
}
