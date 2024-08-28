package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.*;
import me.hwangje.smart_farm.dto.ControllerDto;
import me.hwangje.smart_farm.dto.DeviceTimerDto.UpdateDeviceTimerRequest;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.DeviceTimerRepository;
import me.hwangje.smart_farm.repository.GroupRepository;
import me.hwangje.smart_farm.repository.UserRepository;
import me.hwangje.smart_farm.service.ControllerService;
import me.hwangje.smart_farm.service.DeviceTimerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class DeviceTimerApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    DeviceTimerRepository deviceTimerRepository;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    ControllerService controllerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceTimerService deviceTimerService;

    @Autowired
    GroupRepository groupRepository;

    User admin;
    User user;
    Controller testController;
    DeviceTimer testDeviceTimer;
    Group testGroup;
    ControllerDto.AddControllerRequest request;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        testGroup = groupRepository.save(Group.builder()
                .name("Test Group")
                .contact("[0,1,0,9,9,9,9,9,9,9,9]")
                .registrationNumber("[1,2,3,4,5,6,7,8,9,0]")
                .build());

        admin = createUser("admin@test.com", null, Role.ADMIN);
        user = createUser("user@test.com", testGroup, Role.USER);

        request = new ControllerDto.AddControllerRequest(
                "[0,16,1,5,2,3]",
                "테스트2번",
                "[20]",
                "[25]",
                1,
                30,
                1,
                1,
                28,
                18,
                "[0,1,0,1,2,3,4,5,6,7,8]",
                1,
                user.getId()
        );
    }

    private User createUser(String email, Group group, Role role) {
        return userRepository.save(User.builder()
                .email(email)
                .password("test")
                .role(role)
                .name(role.name())
                .group(group)
                .contact("[0,1,0,1,2,3,4,5,6,7,8]")
                .build());
    }

    private void setAuthentication(User user) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    private Controller createController(ControllerDto.AddControllerRequest request) throws Exception {
        setAuthentication(admin);

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/controllers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        return controllerRepository.findByControllerId(request.getControllerId())
                .orElseThrow(() -> new IllegalArgumentException("Controller not found"));
    }

    @DisplayName("컨트롤러 생성 시 기본 디바이스 타이머가 함께 생성된다")
    @Test
    void createController_CreatesDefaultDeviceTimers() throws Exception {
        // Given & When
        testController = createController(request);

        // Then
        assertThat(testController).isNotNull();
        assertThat(deviceTimerRepository.findAllByController_Id(testController.getId()))
                .hasSize(16)
                .allSatisfy(timer -> {
                    assertThat(timer.getController().getId()).isEqualTo(testController.getId());
                    assertThat(timer.getTimerId()).isIn(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
                });
    }

    @DisplayName("컨트롤러 ID로 모든 디바이스 타이머를 조회한다")
    @Test
    void findAllDeviceTimers_Success() throws Exception {
        // Given
        testController = createController(request);

        // When
        ResultActions result = mockMvc.perform(get("/api/device-timers/{controllerId}", testController.getId()));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(16)))
                .andExpect(jsonPath("$[0].timerId", is(0)));
    }

    @DisplayName("디바이스 타이머 정보를 수정한다")
    @Test
    void updateDeviceTimer_Success() throws Exception {
        // Given
        testController = createController(request);
        testDeviceTimer = deviceTimerRepository.findAllByController_Id(testController.getId()).get(0);

        UpdateDeviceTimerRequest updateRequest = new UpdateDeviceTimerRequest(
                testDeviceTimer.getTimerId(),
                testDeviceTimer.getTimer(),
                "조까"
        );
        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // When
        ResultActions result = mockMvc.perform(put("/api/device-timers/{id}", testDeviceTimer.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("조까")));
    }

    @DisplayName("컨트롤러 삭제 시 관련 디바이스 타이머가 함께 삭제된다")
    @Test
    @Transactional
    void deleteController_deleteRelatedDeviceTimers() throws Exception {
        // Given
        testController = createController(request);
        Long controllerId = testController.getId();
        assertThat(deviceTimerRepository.findAllByController_Id(controllerId)).isNotEmpty();

        // When
        ResultActions result = mockMvc.perform(delete("/api/controllers/{id}", controllerId));

        // Then
        result.andExpect(status().isOk());

        assertThat(controllerRepository.findById(controllerId)).isEmpty();
        assertThat(deviceTimerRepository.findAllByController_Id(controllerId)).isEmpty();
    }
}