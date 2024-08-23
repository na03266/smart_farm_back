package me.hwangje.smart_farm;

import me.hwangje.smart_farm.service.MqttService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.integration.config.EnableIntegration;

@EnableJpaAuditing
@SpringBootApplication
@EnableIntegration
public class SpringBootDeveloperApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootDeveloperApplication.class, args);
        // 종료 훅 추가
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            MqttService mqttService = context.getBean(MqttService.class);
            mqttService.shutdown();
            context.close();
        }));
    }
}