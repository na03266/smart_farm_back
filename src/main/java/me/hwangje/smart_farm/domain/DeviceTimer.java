package me.hwangje.smart_farm.domain;

import jakarta.persistence.*;
import lombok.*;
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
    private Long id;

    @Column(name = "timer_id")
    private Integer timerId;

    @Column(name = "timer")
    private String timer;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id")
    private Controller controller;

    @Builder
    public DeviceTimer(Integer timerId, String timer, Controller controller) {
        this.timerId = timerId;
        this.timer = timer;
        this.controller = controller;
    }

    public void update(Integer timerId, String timer, Controller controller) {
        this.timerId = timerId;
        this.timer = timer;
        this.controller = controller;
    }
}