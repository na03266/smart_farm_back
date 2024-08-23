package me.hwangje.smart_farm.service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public MqttService(MqttPahoMessageDrivenChannelAdapter mqttInbound,
                       MqttPahoMessageHandler mqttOutbound) {
        this.mqttInbound = mqttInbound;
        this.mqttOutbound = mqttOutbound;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();

        System.out.println("Received message from topic '" + topic + "': " + payload);

        if (topic.startsWith("sensor/")) {
            // 센서 데이터 처리
        } else if (topic.startsWith("control/")) {
            // 제어 명령 처리
        }
        // ... 기타 토픽 처리 ...
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