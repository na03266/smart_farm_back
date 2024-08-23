package me.hwangje.smart_farm.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MqttServiceTest {

    @Autowired
    private MqttService mqttService;

    @Test
    public void testPublishAndReceive() {
        // 메시지 발행
        mqttService.publishMessage("test/topic", "Hello, MQTT!");

        // 실제로 메시지가 수신되는지 확인하기 위해 대기
        try {
            Thread.sleep(2000); // 2초 대기
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 대기 후 로그를 확인하세요.
    }

    @AfterAll
    public void tearDown() {
        mqttService.shutdown();
    }
}