package me.hwangje.smart_farm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.repository.ControllerRepository;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MqttService {

    private final MqttPahoMessageDrivenChannelAdapter mqttInbound;
    private final MqttPahoMessageHandler mqttOutbound;
    private final ObjectMapper objectMapper;
    private final ControllerRepository controllerRepository;
    private final SetupService setupService;

    @Transactional
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();

        log.info("Received message from topic '{}': {}", topic, payload);

        if (topic == null) {
            log.error("Received message with null topic");
            return;
        }

        String[] topicParts = topic.split("/");

        if (topicParts.length >= 2) {
            String mainTopic = topicParts[0];
            String subTopic = topicParts[1];

            if (mainTopic.equals("SMARTFARM")) {
                handleSmartFarmTopic(subTopic, payload);
            } else {
                log.warn("Unknown main topic: {}", mainTopic);
            }
        } else {
            log.error("Invalid topic format: {}", topic);
        }
    }

    private void handleSmartFarmTopic(String subTopic, String payload) {
        switch (subTopic) {
            case "DEVICE_STATUS":
                handleDeviceStatusTopic(payload);
                break;
            case "SENSOR_DATA":
                handleSensorDataTopic(payload);
                break;
            case "SETUP":
                handleSetupTopic(payload);
                break;
            default:
                System.out.println("Unknown SMARTFARM sub-topic: " + subTopic);
        }
    }

    // 최초 입력시 컨트롤러 없으면 생성하는걸 해야할까?
    // 아님 그냥 생성되어있는거만 넣을수 있도록 해야할까?
    protected void handleSetupTopic(String payload) {
        try {
            JsonNode rootNode = objectMapper.readTree(payload);
            // 디바이스 설정 정보 저장
            JsonNode setDeviceNode = rootNode.get("setdevice");
            // 디바이스 타이머 정보 저장
            JsonNode deviceTimerNode = rootNode.get("UTIMER");
            // 디바이스 설정 정보 저장
            JsonNode setSensorNode = rootNode.get("setsensor");

            // 값 전달 받을 시 한 문자열로 받도록 요청
            Controller controller = controllerRepository.findByControllerId(rootNode.get("CID").asText())
                    .orElseThrow(() -> new IllegalArgumentException("Controller not found"));

            setupService.handleSetup(controller, rootNode, setDeviceNode, deviceTimerNode, setSensorNode);
            log.info("Setup completed for controller: {}", controller.getControllerId());

        } catch (Exception e) {
            log.error("Error parsing controller data", e);
        }

    }


    private void handleDeviceStatusTopic(String payload) {
        // SETUP 관련 처리
    }

    private void handleSensorDataTopic(String payload) {
        // SETUP 관련 처리
    }

    public void publishMessage(String topic, String payload) {
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .build();
        mqttOutbound.handleMessage(message);
        System.out.println("Published message to topic '" + topic + "': " + payload);
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (mqttInbound != null) {
                mqttInbound.stop();
            }
            if (mqttOutbound != null) {
                mqttOutbound.stop();
            }
        } catch (Exception e) {
            System.err.println("Error occurred while shutting down MQTT connections: " + e.getMessage());
        }
    }
}