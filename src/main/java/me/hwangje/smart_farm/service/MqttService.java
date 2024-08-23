package me.hwangje.smart_farm.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class MqttService {

    private final MessageChannel mqttOutboundChannel;

    public MqttService(@Qualifier("mqttOutboundChannel") MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();

        System.out.println("Received message from topic '" + topic + "': " + payload);

        /**
         * 수신할때 어떤 자료형으로 수신하는지 확인 후에
         * 각 제어명령에 따라서 수신할것,
         * 제어명령 setup, device-status, sensor-data
         */
        // 토픽에 따른 처리 로직
        if (topic.startsWith("sensor/")) {
            // 센서 데이터 처리
        } else if (topic.startsWith("control/")) {
            // 제어 명령 처리
        }
        // ... 기타 토픽 처리 ...
    }

    // 이걸 가져다가 다른데서 참조해서 실행시켜야함. 대신 json 구조화 필수
    public void publishMessage(String topic, String payload) {
        Message<String> message = MessageBuilder
                .withPayload(payload)
                .setHeader(MqttHeaders.TOPIC, topic)
                .build();
        mqttOutboundChannel.send(message);
        System.out.println("Published message to topic '" + topic + "': " + payload);
    }
}