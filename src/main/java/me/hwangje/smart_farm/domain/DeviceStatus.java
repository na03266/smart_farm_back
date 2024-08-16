package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "device_statuses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DeviceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "unit_id")
    private Integer unitId;

    @Column(name = "is_auto_mode")
    private Boolean isAutoMode;

    @Column(name = "status")
    private Integer status;

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
    public String getControllerId() {
        return controller != null ? controller.getControllerId() : null;
    }

    @Builder
    public DeviceStatus(Integer unitId, Boolean isAutoMode, Integer status, Controller controller) {
        this.unitId = unitId;
        this.isAutoMode = isAutoMode;
        this.status = status;
        this.controller = controller;
    }

    public void update(Integer unitId, Boolean isAutoMode, Integer status) {
        this.unitId = unitId;
        this.isAutoMode = isAutoMode;
        this.status = status;
    }
}