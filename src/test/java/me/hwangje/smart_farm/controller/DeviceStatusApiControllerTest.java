package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.*;
import me.hwangje.smart_farm.dto.ControllerDto;
import me.hwangje.smart_farm.dto.DeviceStatusDto.UpdateDeviceStatusRequest;
import me.hwangje.smart_farm.repository.*;
import me.hwangje.smart_farm.service.ControllerService;
import me.hwangje.smart_farm.service.DeviceStatusService;
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
class DeviceStatusApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    DeviceStatusRepository deviceStatusRepository;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    ControllerService controllerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DeviceStatusService deviceStatusService;

    @Autowired
    GroupRepository groupRepository;

    User admin;
    User user;
    Controller testController;
    DeviceStatus testDeviceStatus;
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
                .contact("01099999999")
                .registrationNumber("1234567890")
                .build());

        admin = createUser("admin@test.com", null, Role.ADMIN);
        user = createUser("user@test.com", testGroup, Role.USER);

        request = new ControllerDto.AddControllerRequest(
                "TEST_CTRL_002",
                "테스트2번",
                "20.0f",
                "25.0f",
                1,
                30,
                1,
                1,
                28,
                18,
                "01012345678",
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
                .contact("01012345678")
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

    @DisplayName("컨트롤러 생성 시 기본 디바이스 상태가 함께 생성된다")
    @Test
    void createController_CreatesDefaultDeviceStatuses() throws Exception {
        // Given & When
        testController = createController(request);

        // Then
        assertThat(testController).isNotNull();
        assertThat(deviceStatusRepository.findAllByController_Id(testController.getId()))
                .hasSize(16)
                .allSatisfy(device -> {
                    assertThat(device.getController().getId()).isEqualTo(testController.getId());
                    assertThat(device.getUnitId()).isIn(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
                });
    }

    @DisplayName("컨트롤러 ID로 모든 디바이스 상태를 조회한다")
    @Test
    void findAllDeviceStatuses() throws Exception {
        // Given
        testController = createController(request);

        // When
        ResultActions result = mockMvc.perform(get("/api/device-statuses/{controllerId}", testController.getId()));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(16)))
                .andExpect(jsonPath("$[0].status", is(0)));
    }

    @DisplayName("디바이스 상태 정보를 수정한다")
    @Test
    void updateDeviceStatus() throws Exception {
        // Given
        testController = createController(request);
        testDeviceStatus = deviceStatusRepository.findAllByController_Id(testController.getId()).get(0);

        UpdateDeviceStatusRequest updateRequest = new UpdateDeviceStatusRequest(
                testDeviceStatus.getUnitId(),
                true,
                1
        );
        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // When
        ResultActions result = mockMvc.perform(put("/api/device-statuses/{id}", testDeviceStatus.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.isAutoMode", is(true)))
                .andExpect(jsonPath("$.status", is(1)));
    }

    @DisplayName("컨트롤러 삭제 시 관련 디바이스 상태가 함께 삭제된다")
    @Test
    void deleteController_deleteRelatedDeviceStatuses() throws Exception {
        // Given
        testController = createController(request);
        Long controllerId = testController.getId();
        assertThat(deviceStatusRepository.findAllByController_Id(controllerId)).isNotEmpty();

        // When
        ResultActions result = mockMvc.perform(delete("/api/controllers/{id}", testController.getId()));

        // Then
        result.andExpect(status().isOk());

        assertThat(controllerRepository.findById(testController.getId())).isEmpty();
        assertThat(deviceStatusRepository.findAllByController_Id(controllerId)).isEmpty();
    }
}