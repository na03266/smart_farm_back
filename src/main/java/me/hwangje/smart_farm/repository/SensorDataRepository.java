package me.hwangje.smart_farm.repository;

import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.SensorData;
import me.hwangje.smart_farm.dto.SensorDataDto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
    List<SensorData> findAllByController_Id(Long controllerId);

    @Query(value =
            "SELECT " +
                    "   sd.sensor_id AS sensorId, " +
                    "   AVG(sd.sensor_value) AS averageValue, " +
                    "   DATE_ADD(" +
                    "      DATE(sd.recorded_at), " +
                    "      INTERVAL (FLOOR(MINUTE(sd.recorded_at) / 30) * 30) MINUTE" +
                    "   ) AS timeBlock " +
                    "FROM sensor_data sd " +
                    "WHERE sd.controller_id = :controllerId " +
                    "   AND sd.recorded_at BETWEEN :startTime AND :endTime " +
                    "GROUP BY sd.sensor_id, timeBlock " +
                    "ORDER BY timeBlock, sd.sensor_id",
            nativeQuery = true)
    List<SensorDataAverage> findAveragesByControllerIdAndTimeRange(
            @Param("controllerId") Long controllerId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    @Query(value =
            "SELECT " +
                    "   sd.sensor_id AS sensorId, " +
                    "   AVG(sd.sensor_value) AS averageValue, " +
                    "   CAST(sd.recorded_at AS DATE) AS date " +
                    "FROM sensor_data sd " +
                    "WHERE sd.controller_id = :controllerId " +
                    "   AND CAST(sd.recorded_at AS DATE) BETWEEN :startDate AND :endDate " +
                    "GROUP BY sd.sensor_id, CAST(sd.recorded_at AS DATE) " +
                    "ORDER BY CAST(sd.recorded_at AS DATE), sd.sensor_id",
            nativeQuery = true)
    List<SensorDataAverage> findDailyAveragesByControllerIdAndDateRange(
            @Param("controllerId") Long controllerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT sd FROM SensorData sd " +
            "WHERE sd.controller.id = :controllerId AND sd.recordedAt = " +
            "(SELECT MAX(sd2.recordedAt) FROM SensorData sd2 " +
            "WHERE sd2.sensorId = sd.sensorId AND sd2.controller.id = :controllerId)")
    List<SensorData> findLatestByControllerId(@Param("controllerId") Long controllerId);


    void deleteAllByController(Controller controller);

    Optional<SensorData> findBySensorIdAndControllerId(int i, String controllerId);
}