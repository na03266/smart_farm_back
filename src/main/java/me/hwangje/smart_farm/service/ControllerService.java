package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.ControllerDto.AddControllerRequest;
import me.hwangje.smart_farm.dto.ControllerDto.UpdateControllerRequest;
import me.hwangje.smart_farm.dto.DeviceSetupDto;
import me.hwangje.smart_farm.dto.DeviceStatusDto;
import me.hwangje.smart_farm.dto.DeviceTimerDto;
import me.hwangje.smart_farm.dto.SensorSetupDto;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ControllerService {
    private final ControllerRepository controllerRepository;
    private final UserRepository userRepository;
    @Lazy
    private final DeviceSetupService deviceSetupService;
    @Lazy
    private final DeviceTimerService deviceTimerService;
    @Lazy
    private final SensorSetupService sensorSetupService;
    @Lazy
    private final DeviceStatusService deviceStatusService;

    // Create
    @Transactional
    public Controller save(AddControllerRequest request) {
        // 입력받은 유저 ID를 바탕으로
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = Controller.builder()
                .controllerId(request.getControllerId())
                .name(request.getName())
                .setTempLow(request.getSetTempLow())
                .setTempHigh(request.getSetTempHigh())
                .tempGap(request.getTempGap())
                .heatTemp(request.getHeatTemp())
                .iceType(request.getIceType())
                .alarmType(request.getAlarmType())
                .alarmTempHigh(request.getAlarmTempHigh())
                .alarmTempLow(request.getAlarmTempLow())
                .tel(request.getTel())
                .awsEnabled(request.getAwsEnabled())
                .user(user)
                .build();
        return controllerRepository.save(controller);
    }

    // Read (단일 컨트롤러)
    public Controller findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = controllerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Controller not found with id: " + id));


        if (controller.getUserId().equals(user.getId()) || user.getRole().equals(Role.ADMIN)) {
            // 관리자거나 등록된 사용자거나
            return controller;
        } else if (user.getRole().equals(Role.MANAGER) && user.getGroup().equals(controller.getUser().getGroup())) {
            //매니저이고 컨트롤러와 같은 그룹인경우
            return controller;
        } else if (controller.getUser().equals(user)) {
            // 컨트롤러의 소유자인 경우
            return controller;
        }
        throw new IllegalArgumentException("You don't have permission to access this controller.");


    }

    // Read (모든 컨트롤러)
    public List<Controller> findAllByRole(String controllerNameLike, String userNameLike, String groupNameLike) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole().equals(Role.ADMIN)) {
            // 관리자인 경우
            return controllerRepository.searchControllers(
                    controllerNameLike, userNameLike, groupNameLike);
        } else if (user.getRole().equals(Role.MANAGER)) {
            // 매니저인 경우
            return controllerRepository.searchControllers(
                    controllerNameLike, userNameLike, user.getGroup().getName());
        } else {
            // 사용자인 경우
            return controllerRepository.searchControllers(
                    controllerNameLike, user.getName(), user.getGroup().getName());
        }
    }

    // Update
    @Transactional
    public Controller update(Long id, UpdateControllerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = findById(id);

        if (controller.getUserId().equals(user.getId())
                || user.getRole().equals(Role.ADMIN)
                || (user.getGroup().equals(controller.getUser().getGroup())
                && user.getRole().equals(Role.MANAGER))) {
            controller.update(
                    request.getName() != null ? request.getName() : controller.getName(),
                    request.getSetTempLow() != null ? request.getSetTempLow() : controller.getSetTempLow(),
                    request.getSetTempHigh() != null ? request.getSetTempHigh() : controller.getSetTempHigh(),
                    request.getTempGap() != null ? request.getTempGap() : controller.getTempGap(),
                    request.getHeatTemp() != null ? request.getHeatTemp() : controller.getHeatTemp(),
                    request.getIceType() != null ? request.getIceType() : controller.getIceType(),
                    request.getAlarmType() != null ? request.getAlarmType() : controller.getAlarmType(),
                    request.getAlarmTempHigh() != null ? request.getAlarmTempHigh() : controller.getAlarmTempHigh(),
                    request.getAlarmTempLow() != null ? request.getAlarmTempLow() : controller.getAlarmTempLow(),
                    request.getTel() != null ? request.getTel() : controller.getTel(),
                    request.getAwsEnabled() != null ? request.getAwsEnabled() : controller.getAwsEnabled(),
                    request.getUserId() != null ? userRepository.findById(request.getUserId()).orElse(controller.getUser()) : controller.getUser()
            );

            return controller;
        }
        throw new IllegalArgumentException("You don't have permission to update this controller.");

    }

    // Delete
    @Transactional
    public void delete(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = controllerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Controller not found with id: " + id));

        if (user.getRole().equals(Role.USER)) {
            throw new IllegalArgumentException("You don't have permission to delete this controller.");
        }
        deviceTimerService.deleteAllByController(controller);
        deviceSetupService.deleteAllByController(controller);
        sensorSetupService.deleteAllByController(controller);
        deviceStatusService.deleteAllByController(controller);

        controllerRepository.delete(controller);
    }

    @Transactional
    public void createDefaultDeviceSetups(Controller controller) {
        for (int i = 0; i < 16; i++) {
            DeviceSetupDto.AddDeviceSetupRequest deviceSetupRequest = DeviceSetupDto.AddDeviceSetupRequest.builder()
                    .unitId(i)
                    .unitType(0)
                    .unitCh(0)
                    .unitOpenCh(0)
                    .unitCloseCh(0)
                    .unitMoveTime(0)
                    .unitStopTime(0)
                    .unitOpenTime(0)
                    .unitCloseTime(0)
                    .operationType(0)
                    .timerSet(0)
                    .build();

            deviceSetupService.save(deviceSetupRequest, controller);
        }
    }

    @Transactional
    public void createDefaultDeviceTimers(Controller controller) {
        String defaultTimer = String.join("", Collections.nCopies(1440, "0"));

        for (int i = 0; i < 16; i++) {
            DeviceTimerDto.AddDeviceTimerRequest deviceTimerRequest = DeviceTimerDto.AddDeviceTimerRequest.builder()
                    .timerId(i)
                    .timer(defaultTimer)
                    .name((i) + "번")  // 1번부터 16번까지
                    .build();

            deviceTimerService.save(deviceTimerRequest, controller);
        }
    }

    @Transactional
    public void createDefaultSensorSetup(Controller controller) {
        for (int i = 0; i < 9; i++) {
            SensorSetupDto.AddSensorSetupRequest sensorSetupRequest = SensorSetupDto.AddSensorSetupRequest.builder()
                    .sensorId(i)
                    .sensorCh(0)
                    .sensorReserved(0)
                    .sensorMult(0.0F)
                    .sensorOffset(0.0F)
                    .sensorFormula("")
                    .build();
            sensorSetupService.save(sensorSetupRequest, controller);
        }
    }

    @Transactional
    public void createDefaultDeviceStatus(Controller controller) {
        for (int i = 0; i < 16; i++) {
            DeviceStatusDto.AddDeviceStatusRequest deviceStatusRequest = DeviceStatusDto.AddDeviceStatusRequest.builder()
                    .unitId(i)
                    .status(0)
                    .isAutoMode(false)
                    .build();

            deviceStatusService.save(deviceStatusRequest, controller);
        }
    }
}