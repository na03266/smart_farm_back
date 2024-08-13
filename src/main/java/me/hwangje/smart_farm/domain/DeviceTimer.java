package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Table(name = "device_timers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class DeviceTimer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "timer_id")
    private int timerId;

    @Lob
    @Column(name = "timer")
    private String timer;

    @Column(name = "name")
    private String name;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "controller_id",nullable = false)
    private Controller controller;

    @JsonProperty("controllerId")
    public Long getControllerId(){
        return controller != null ? controller.getId() : null;
    }

    @Builder
    public DeviceTimer(Integer timerId, String timer, Controller controller, String name) {
        this.timerId = timerId;
        this.timer = timer;
        this.controller = controller;
        this.name = name;
    }

    public void update(Integer timerId, String timer, String name) {
        this.timerId = timerId;
        this.timer = timer;
        this.name = name;
    }
}