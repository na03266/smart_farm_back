package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.*;
import me.hwangje.smart_farm.dto.ControllerDto;
import me.hwangje.smart_farm.dto.SensorSetupDto.*;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.SensorSetupRepository;
import me.hwangje.smart_farm.repository.GroupRepository;
import me.hwangje.smart_farm.repository.UserRepository;
import me.hwangje.smart_farm.service.ControllerService;
import me.hwangje.smart_farm.service.SensorSetupService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SensorSetupApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    SensorSetupRepository deviceSetupRepository;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    ControllerService controllerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SensorSetupService deviceTimerService;

    @Autowired
    GroupRepository groupRepository;

    User admin;
    User user;
    Controller testController;
    SensorSetup testSensorSetup;
    Group testGroup;
    ControllerDto.AddControllerRequest request;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        deviceSetupRepository.deleteAll();
        controllerRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();

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

    @DisplayName("컨트롤러 생성 시 기본 센서 셋업이 함께 생성된다")
    @Test
    void createController_CreatesDefaultSensorSetups() throws Exception {
        // Given & When
        testController = createController(request);

        // Then
        assertThat(testController).isNotNull();
        assertThat(deviceSetupRepository.findAllByController_Id(testController.getId()))
                .hasSize(9)
                .allSatisfy(sensor -> {
                    assertThat(sensor.getController().getId()).isEqualTo(testController.getId());
                    assertThat(sensor.getSensorId()).isIn(0, 1, 2, 3, 4, 5, 6, 7, 8);
                });
    }

    @DisplayName("컨트롤러 ID로 모든 센서 셋업을 조회한다")
    @Test
    void findAllSensorSetups() throws Exception {
        // Given
        testController = createController(request);

        // When
        ResultActions result = mockMvc.perform(get("/api/sensor-setups/{controllerId}", testController.getId()));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(9)))
                .andExpect(jsonPath("$[0].sensorId", is(0)));
    }

    @DisplayName("센서 셋업 정보를 수정한다")
    @Test
    void updateSensorSetup() throws Exception {
        // Given
        testController = createController(request);
        testSensorSetup = deviceSetupRepository.findAllByController_Id(testController.getId()).get(0);

        UpdateSensorSetupRequest updateRequest = new UpdateSensorSetupRequest(
                testSensorSetup.getSensorId(),
                testSensorSetup.getSensorCh(),
                testSensorSetup.getSensorReserved(),
                testSensorSetup.getSensorMult(),
                testSensorSetup.getSensorOffset(),
                "희희"
        );
        String requestBody = objectMapper.writeValueAsString(updateRequest);

        // When
        ResultActions result = mockMvc.perform(put("/api/sensor-setups/{id}", testSensorSetup.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.sensorFormula", is("희희")));
    }

    @DisplayName("컨트롤러 삭제 시 관련 센서 셋업이 함께 삭제된다")
    @Test
    @Transactional
    void deleteController_deleteRelatedSensorSetups() throws Exception {
        // Given
        testController = createController(request);
        Long controllerId = testController.getId();
        assertThat(deviceSetupRepository.findAllByController_Id(controllerId)).isNotEmpty();

        // When
        ResultActions result = mockMvc.perform(delete("/api/controllers/{id}", controllerId));

        // Then
        result.andExpect(status().isOk());

        assertThat(controllerRepository.findById(controllerId)).isEmpty();
        assertThat(deviceSetupRepository.findAllByController_Id(controllerId)).isEmpty();
    }
}