package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "controllers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Controller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "controller_id", unique = true, nullable = false)
    private String controllerId;

    @Column(name = "name")
    private String name;

    @Column(name = "set_temp_low")
    private Float setTempLow;

    @Column(name = "set_temp_high")
    private Float setTempHigh;

    @Column(name = "temp_gap")
    private Float tempGap;

    @Column(name = "heat_temp")
    private Float heatTemp;

    @Column(name = "ice_type")
    private Integer iceType;

    @Column(name = "alarm_type")
    private Integer alarmType;

    @Column(name = "alarm_temp_high")
    private Float alarmTempHigh;

    @Column(name = "alarm_temp_low")
    private Float alarmTempLow;

    @Column(name = "tel")
    private String tel;

    @Column(name = "aws_enabled")
    private Boolean awsEnabled;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonIgnore
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @JsonProperty("userId")
    public Long getUserId() {
        return user != null ? user.getId() : null;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "controller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceSetup> deviceSetups = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "controller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceTimer> deviceTimers = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "controller", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorSetup> sensorsSetups = new ArrayList<>();

    @Builder
    public Controller(String controllerId, Float setTempLow, Float setTempHigh, Float tempGap, Float heatTemp,
                      Integer iceType, Integer alarmType, Float alarmTempHigh, Float alarmTempLow,
                      String tel, Boolean awsEnabled, User user, String name) {
        this.controllerId = controllerId;
        this.setTempLow = setTempLow;
        this.setTempHigh = setTempHigh;
        this.tempGap = tempGap;
        this.heatTemp = heatTemp;
        this.iceType = iceType;
        this.alarmType = alarmType;
        this.alarmTempHigh = alarmTempHigh;
        this.alarmTempLow = alarmTempLow;
        this.tel = tel;
        this.awsEnabled = awsEnabled;
        this.user = user;
        this.name = name;
    }

    public void update(String name, Float setTempLow, Float setTempHigh, Float tempGap, Float heatTemp,
                       Integer iceType, Integer alarmType, Float alarmTempHigh, Float alarmTempLow,
                       String tel, Boolean awsEnabled, User user) {
        this.name = name;
        this.setTempLow = setTempLow;
        this.setTempHigh = setTempHigh;
        this.tempGap = tempGap;
        this.heatTemp = heatTemp;
        this.iceType = iceType;
        this.alarmType = alarmType;
        this.alarmTempHigh = alarmTempHigh;
        this.alarmTempLow = alarmTempLow;
        this.tel = tel;
        this.awsEnabled = awsEnabled;
        this.user = user;
    }
}