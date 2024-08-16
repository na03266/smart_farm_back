package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "sensor_data")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SensorData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("센서 데이터 고유 번호")
    private Long id;

    @Column(name = "sensor_id")
    @Comment("센서 ID")
    private Integer sensorId;

    @Column(name = "sensor_value")
    @Comment("센서값")
    private Float sensorValue;

    @CreatedDate
    @Column(name = "recorded_at")
    @Comment("기록 시간")
    private LocalDateTime recordedAt;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id")
    private Controller controller;

    @JsonProperty("controllerId")
    public String getControllerId() {
        return controller != null ? controller.getControllerId() : null;
    }

    @Builder
    public SensorData(Integer sensorId, Float sensorValue, Controller controller) {
        this.sensorId = sensorId;
        this.sensorValue = sensorValue;
        this.controller = controller;
    }
}