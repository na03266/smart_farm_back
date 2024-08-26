package me.hwangje.smart_farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.DeviceSetupRepository;
import me.hwangje.smart_farm.repository.SensorSetupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Service
public class MqttService {

    private final MqttPahoMessageDrivenChannelAdapter mqttInbound;
    private final MqttPahoMessageHandler mqttOutbound;

    @Lazy
    private final ObjectMapper objectMapper;
    @Lazy
    private final ControllerRepository controllerRepository;
    @Lazy
    private final DeviceSetupRepository deviceSetupRepository;
    @Lazy
    private final SensorSetupRepository sensorSetupRepository;


    @Autowired
    public MqttService(MqttPahoMessageDrivenChannelAdapter mqttInbound,
                       MqttPahoMessageHandler mqttOutbound,
                       DeviceSetupRepository deviceSetupRepository,
                       ControllerRepository controllerRepository,
                       ObjectMapper objectMapper,
                       SensorSetupRepository sensorSetupRepository
    ) {
        this.mqttInbound = mqttInbound;
        this.mqttOutbound = mqttOutbound;
        this.deviceSetupRepository = deviceSetupRepository;
        this.controllerRepository = controllerRepository;
        this.objectMapper = objectMapper;
        this.sensorSetupRepository = sensorSetupRepository;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();

        System.out.println("Received message from topic '" + topic + "': " + payload);

        assert topic != null;
        String[] topicParts = topic.split("/");

        if (topicParts.length >= 2) {
            String mainTopic = topicParts[0];
            String subTopic = topicParts[1];

            if (mainTopic.equals("SMARTFARM")) {
                handleSmartFarmTopic(subTopic, payload);
            } else {
                System.out.println("Unknown main topic: " + mainTopic);
            }
        } else {
            System.out.println("Invalid topic format: " + topic);
        }
    }

    private void handleSmartFarmTopic(String subTopic, String payload) {
        switch (subTopic) {
            case "DEVICE_STATUS":
                // 디바이스 상태 처리
                break;
            case "SENSOR_DATA":
                // 센서 데이터 처리
                break;
            case "SETUP":
                // 센서 데이터 처리
                break;
            default:
                System.out.println("Unknown SMARTFARM sub-topic: " + subTopic);
        }
    }

    private void handleSetupTopic(String payload) {
        // SETUP 관련 처리

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