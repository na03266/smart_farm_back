package me.hwangje.smart_farm.service;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.*;
import me.hwangje.smart_farm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MqttSubscribeService {
    private final DeviceSetupRepository deviceSetupRepository;
    private final SensorSetupRepository sensorSetupRepository;
    private final DeviceTimerRepository deviceTimerRepository;
    private final DeviceStatusRepository deviceStatusRepository;
    private final SensorDataRepository sensorDataRepository;

    @Transactional
    public void handleSetup(Controller controller, JsonNode rootNode, JsonNode setDeviceNode, JsonNode deviceTimerNode, JsonNode setSensorNode) {
        updateController(controller, rootNode);
        updateDevice(controller, setDeviceNode);
        updateTimer(controller, deviceTimerNode);
        updateSensor(controller, setSensorNode);
    }

    private void updateController(Controller controller, JsonNode rootNode) {
        controller.update(
                controller.getName(),
                rootNode.get("settempL").toString(), // 문자열로 저장하는게 나을지도?
                rootNode.get("settempH").toString(),
                rootNode.get("TEMPGAP").intValue(),
                rootNode.get("HEATTEMP").intValue(),
                rootNode.get("ICETYPE").intValue(),
                rootNode.get("ALARMTYPE").intValue(),
                rootNode.get("ALRAMTEMPH").intValue(),
                rootNode.get("ALRAMTEMPL").intValue(),
                rootNode.get("TEL").toString(),
                rootNode.get("AWSBIT").asInt(),
                controller.getUser()
        );

    }

    private void updateDevice(Controller controller, JsonNode setDeviceNode) {
        for (int i = 0; i < setDeviceNode.size(); i++) {
            JsonNode deviceNode = setDeviceNode.get(i);
            DeviceSetup deviceSetup = deviceSetupRepository.findByControllerAndUnitId(controller, i)
                    .orElseThrow(() -> new IllegalArgumentException("Controller & Device not found"));

            deviceSetup.update(
                    deviceNode.get("UID").intValue(),
                    deviceNode.get("UTYPE").intValue(),
                    deviceNode.get("UCH").intValue(),
                    deviceNode.get("UOPENCH").intValue(),
                    deviceNode.get("UCLOSECH").intValue(),
                    deviceNode.get("UMVTIME").intValue(),
                    deviceNode.get("USTTIME").intValue(),
                    deviceNode.get("UOPENTIME").intValue(),
                    deviceNode.get("UCLOSETIME").intValue(),
                    deviceNode.get("UOPTYPE").intValue(),
                    deviceNode.get("UTIMERSET").intValue()
            );
        }

    }

    private void updateTimer(Controller controller, JsonNode deviceTimerNode) {
        List<DeviceTimer> deviceTimersToUpdate = new ArrayList<>();

        for (int i = 0; i < deviceTimerNode.size(); i++) {
            JsonNode timerNode = deviceTimerNode.get(i);
            DeviceTimer deviceTimer = deviceTimerRepository.findByControllerAndTimerId(controller, i)
                    .orElseThrow(() -> new IllegalArgumentException("Controller & Timer not found"));

            deviceTimer.update(
                    deviceTimer.getTimerId(),
                    timerNode.toString(),
                    deviceTimer.getName()
            );

            // 업데이트된 엔티티를 리스트에 추가
            deviceTimersToUpdate.add(deviceTimer);
        }

        // 모든 업데이트된 엔티티를 한 번에 저장
        deviceTimerRepository.saveAll(deviceTimersToUpdate);
    }

    private void updateSensor(Controller controller, JsonNode setSensorNode) {
        for (int i = 0; i < setSensorNode.size(); i++) {
            JsonNode sensorNode = setSensorNode.get(i);
            SensorSetup sensorSetup = sensorSetupRepository.findByControllerAndSensorId(controller, i)
                    .orElseThrow(() -> new IllegalArgumentException("Controller & Sensor not found"));

            sensorSetup.update(
                    sensorNode.get("SID").intValue(),
                    sensorNode.get("SCH").intValue(),
                    sensorNode.get("SRESERVED").intValue(),
                    sensorNode.get("SMULT").floatValue(),
                    sensorNode.get("SOFFSET").floatValue(),
                    sensorNode.get("SEQ").toString()
            );
        }
    }

    @Transactional
    public void handleDeviceStatus (Controller controller, JsonNode statusNodes){
        for (int i = 0; i < statusNodes.size(); i++){
            JsonNode statusNode = statusNodes.get(i);
            DeviceStatus deviceStatus = deviceStatusRepository.findByUnitIdAndControllerId(i, controller.getControllerId())
                    .orElseThrow(()-> new IllegalArgumentException(" Controller & Sensor not found"));
            deviceStatus.update(
                    statusNode.get("UID").intValue(),
                    statusNode.get("UMODE").intValue(),
                    statusNode.get("USTATUS").intValue()
            );
        }
    }
    @Transactional
    public void handleSensorData (Controller controller, JsonNode dataNodes){
        for (int i = 0; i < dataNodes.size(); i++){
            JsonNode dataNode = dataNodes.get(i);

            SensorData sensorData = SensorData.builder()
                    .sensorId(dataNode.get("SID").intValue())
                    .sensorValue(dataNode.get("value").floatValue())
                    .controller(controller)
                    .build();

            sensorDataRepository.save(sensorData);
        }
    }
}
