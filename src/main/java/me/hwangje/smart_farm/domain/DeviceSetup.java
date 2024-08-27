package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "device_setups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DeviceSetup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "unit_type")
    private Integer unitType;

    @Column(name = "unit_ch")
    private Integer unitCh;

    @Column(name = "unit_open_ch")
    private Integer unitOpenCh;

    @Column(name = "unit_close_ch")
    private Integer unitCloseCh;

    @Column(name = "unit_move_time")
    private Integer unitMoveTime;

    @Column(name = "unit_stop_time")
    private Integer unitStopTime;

    @Column(name = "unit_open_time")
    private Integer unitOpenTime;

    @Column(name = "unit_close_time")
    private Integer unitCloseTime;

    @Column(name = "operation_type")
    private Integer operationType;

    @Column(name = "timer_set")
    private Integer timerSet;

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
    public DeviceSetup(Integer unitId, Integer unitType, Integer unitCh, Integer unitOpenCh,
                       Integer unitCloseCh, Integer unitMoveTime, Integer unitStopTime,
                       Integer unitOpenTime, Integer unitCloseTime, Integer operationType,
                       Integer timerSet, Controller controller) {
        this.unitId = unitId;
        this.unitType = unitType;
        this.unitCh = unitCh;
        this.unitOpenCh = unitOpenCh;
        this.unitCloseCh = unitCloseCh;
        this.unitMoveTime = unitMoveTime;
        this.unitStopTime = unitStopTime;
        this.unitOpenTime = unitOpenTime;
        this.unitCloseTime = unitCloseTime;
        this.operationType = operationType;
        this.timerSet = timerSet;
        this.controller = controller;
    }

    public void update(Integer unitId, Integer hasLightShield, Integer unitCh, Integer unitOpenCh,
                       Integer unitCloseCh, Integer unitMoveTime, Integer unitStopTime,
                       Integer unitOpenTime, Integer unitCloseTime, Integer operationType,
                       Integer timerSet) {
        this.unitId = unitId;
        this.unitType = hasLightShield;
        this.unitCh = unitCh;
        this.unitOpenCh = unitOpenCh;
        this.unitCloseCh = unitCloseCh;
        this.unitMoveTime = unitMoveTime;
        this.unitStopTime = unitStopTime;
        this.unitOpenTime = unitOpenTime;
        this.unitCloseTime = unitCloseTime;
        this.operationType = operationType;
        this.timerSet = timerSet;
    }
}