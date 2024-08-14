package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "sensor_setups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class SensorSetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sensor_id")
    @Comment("컨트롤러별 센서 ID")
    private Integer sensorId;

    @Column(name = "sensor_ch")
    private Integer sensorCh;

    @Column(name = "sensor_reserved")
    private Integer sensorReserved;

    @Column(name = "sensor_mult")
    private Float sensorMult;

    @Column(name = "sensor_offset")
    private Float sensorOffset;

    @Column(name = "conversion_formula")
    private String sensorFormula;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id")
    private Controller controller;

    @JsonProperty("controllerId")
    public Long getControllerId() {
        return controller != null ? controller.getId() : null;
    }

    @Builder
    public SensorSetup(Integer sensorId, Integer sensorCh, Integer sensorReserved,
                       Float sensorMult, Float sensorOffset, String sensorFormula,
                       Controller controller) {
        this.sensorId = sensorId;
        this.sensorCh = sensorCh;
        this.sensorReserved = sensorReserved;
        this.sensorMult = sensorMult;
        this.sensorOffset = sensorOffset;
        this.sensorFormula = sensorFormula;
        this.controller = controller;
    }

    public void update(Integer sensorId, Integer sensorCh, Integer sensorReserved,
                       Float sensorMult, Float sensorOffset, String conversionFormula) {
        this.sensorId = sensorId;
        this.sensorCh = sensorCh;
        this.sensorReserved = sensorReserved;
        this.sensorMult = sensorMult;
        this.sensorOffset = sensorOffset;
        this.sensorFormula = conversionFormula;
    }
}