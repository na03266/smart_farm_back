package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.*;
import me.hwangje.smart_farm.dto.ControllerDto;
import me.hwangje.smart_farm.dto.SensorDataDto.*;
import me.hwangje.smart_farm.repository.*;
import me.hwangje.smart_farm.service.ControllerService;
import me.hwangje.smart_farm.service.SensorDataService;
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

import java.time.LocalDateTime;

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
class SensorDataApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    SensorDataRepository sensorDataRepository;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    ControllerService controllerService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    SensorDataService sensorDataService;

    @Autowired
    GroupRepository groupRepository;

    User admin;
    User user;
    Controller testController;
    SensorData testSensorData;
    Group testGroup;
    ControllerDto.AddControllerRequest request;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        sensorDataRepository.deleteAll();
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
                20.0f,
                25.0f,
                1.0f,
                30.0f,
                1,
                1,
                28.0f,
                18.0f,
                "01012345678",
                true,
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

    @DisplayName("최근 센서 데이터를 조회한다")
    @Test
    void getLatestSensorData() throws Exception {
        // Given
        testController = createController(request);
        testSensorData = sensorDataRepository.save(SensorData.builder()
                .sensorId(1)
                .sensorValue(25.5f)
                .controller(testController)
                .build());

        // When
        ResultActions result = mockMvc.perform(get("/api/sensor-data/latest/{controllerId}", testController.getId()));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sensorId", is(1)))
                .andExpect(jsonPath("$[0].sensorValue", is(25.5)));
    }

    @DisplayName("30분 단위 평균 센서 데이터를 조회한다")
    @Test
    void getAverageSensorData() throws Exception {
        // Given
        testController = createController(request);
        testSensorData = sensorDataRepository.save(SensorData.builder()
                .sensorId(1)
                .sensorValue(25.5f)
                .controller(testController)
                .build());
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = LocalDateTime.now();

        // When
        ResultActions result = mockMvc.perform(get("/api/sensor-data/average/{controllerId}", testController.getId())
                .param("startTime", startTime.toString())
                .param("endTime", endTime.toString()));

        // Then
        result.andExpect(status().isOk());
    }

    @DisplayName("일일 평균 센서 데이터를 조회한다")
    @Test
    void getDailyAverageSensorData() throws Exception {
        // Given
        testController = createController(request);
        testSensorData = sensorDataRepository.save(SensorData.builder()
                .sensorId(1)
                .sensorValue(25.5f)
                .controller(testController)
                .build());
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        // When
        ResultActions result = mockMvc.perform(get("/api/sensor-data/daily-average/{controllerId}", testController.getId())
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()));

        // Then
        result.andExpect(status().isOk());
    }

    @DisplayName("센서 데이터를 삭제한다")
    @Test
    void deleteSensorData() throws Exception {
        // Given
        testController = createController(request);
        testSensorData = sensorDataRepository.save(SensorData.builder()
                .sensorId(1)
                .sensorValue(25.5f)
                .controller(testController)
                .build());

        // When
        ResultActions result = mockMvc.perform(delete("/api/sensor-data/{id}", testSensorData.getId()));

        // Then
        result.andExpect(status().isOk());
        assertThat(sensorDataRepository.findById(testSensorData.getId())).isEmpty();
    }
}