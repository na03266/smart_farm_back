package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.ControllerDto.*;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ControllerService {
    private final ControllerRepository controllerRepository;
    private final UserRepository userRepository;

    // Create
    @Transactional
    public Controller save(AddControllerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = Controller.builder()
                .controllerId(request.getControllerId())
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
                .group(user.getGroup())
                .build();
        return controllerRepository.save(controller);
    }

    // Read (단일 컨트롤러)
    public Controller findById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = controllerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Controller not found with id: " + id));
        if (controller.getUser().equals(user) || user.getRole().equals(Role.ADMIN)) {
            // 관리자거나 등록된 사용자거나
            return controller;
        } else if (controller.getUser().getRole().equals(Role.MANAGER)
                && controller.getGroup().equals(user.getGroup())) {
            //매니저이고 컨트롤러와 같은 그룹인경우
            return controller;
        } else {
            //그 외
            throw new IllegalArgumentException("You don't have permission to access this controller.");
        }
    }

    // Read (모든 컨트롤러)
    public List<Controller> findAll(String controllerNameLike, String userNameLike, String groupNameLike) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole().equals(Role.ADMIN)) {
            return controllerRepository.findAll();
        } else if (user.getRole().equals(Role.MANAGER)) {
            return controllerRepository.findByGroup(user.getGroup());
        } else {
            return controllerRepository.findByUser(user);
        }
    }

    // Update
    @Transactional
    public Controller update(Long id, UpdateControllerRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = findById(id);

        if (controller.getUser().equals(user) || user.getRole().equals(Role.ADMIN)) {
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
                    request.getUser() != null ? request.getUser() : controller.getUser(),
                    request.getGroup() != null ? request.getGroup() : controller.getGroup()
            );
            return controller;
        } else {
            throw new IllegalArgumentException("You don't have permission to update this controller.");
        }
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByName(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Controller controller = controllerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Controller not found with id: " + id));

        if (!user.getRole().equals(Role.USER)) {
            controllerRepository.delete(controller);
        } else {
            throw new IllegalArgumentException("You don't have permission to delete this controller.");
        }
    }
}