package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.ControllerDto.*;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.GroupRepository;
import me.hwangje.smart_farm.repository.UserRepository;
import me.hwangje.smart_farm.service.ControllerService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ControllerApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    ControllerRepository controllerRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    ControllerService controllerService;

    User admin;
    User manager;
    User user;
    Controller testController;
    Group testGroup;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        controllerRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();

        testGroup = groupRepository.save(Group.builder()
                .name("Test Group")
                .contact("01099999999")
                .registrationNumber("1234567890")
                .build());

        admin = createUser("admin@test.com", null, Role.ADMIN);
        manager = createUser("manager@test.com", testGroup, Role.MANAGER);
        user = createUser("user@test.com", testGroup, Role.USER);

        testController = controllerRepository.save(Controller.builder()
                .controllerId("TEST_CTRL_001")
                .name("테스트컨트롤러")
                .setTempLow("20")
                .setTempHigh("25")
                .tempGap(1)
                .heatTemp(30)
                .iceType(1)
                .alarmType(1)
                .alarmTempHigh(28)
                .alarmTempLow(18)
                .tel("01012345678")
                .awsEnabled(1)
                .user(user)
                .build());
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

    @DisplayName("ADMIN이 새로운 컨트롤러를 추가한다")
    @Test
    void addController_Admin_Success() throws Exception {
        // Given
        setAuthentication(admin);
        AddControllerRequest request = new AddControllerRequest(
                "TEST_CTRL_002",
                "테스트2번",
                "20",
                "25",
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
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(post("/api/controllers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.controllerId", is("TEST_CTRL_002")));

        assertThat(controllerRepository.findByControllerId("TEST_CTRL_002")).isPresent();
    }

    @DisplayName("ADMIN, MANAGER 가 아닌 사용자가 컨트롤러를 추가하려고 하면 실패한다")
    @Test
    void addController_NonAdmin_Fail() throws Exception {
        // Given
        setAuthentication(user);
        AddControllerRequest request = new AddControllerRequest(
                "TEST_CTRL_002",
                "테스트2번",
                "20",
                "25",
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
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(post("/api/controllers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isForbidden());
    }

    @DisplayName("모든 사용자가 할당된 컨트롤러를 조회할 수 있다")
    @Test
    void findAllControllers_Success() throws Exception {
        // Given
        setAuthentication(user);

        // When
        ResultActions result = mockMvc.perform(get("/api/controllers"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is(testController.getName())));
    }

    @DisplayName("특정 컨트롤러를 ID로 조회할 수 있다")
    @Test
    void findController_Success() throws Exception {
        // Given
        setAuthentication(user);

        // When
        ResultActions result = mockMvc.perform(get("/api/controllers/{id}", testController.getId()));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(testController.getName())));
    }

    @DisplayName("ADMIN이 컨트롤러 정보를 수정할 수 있다")
    @Test
    void updateController_Admin_Success() throws Exception {
        // Given
        setAuthentication(admin);
        UpdateControllerRequest request = new UpdateControllerRequest(
                "테스트2번",
                "20",
                "25",
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
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/controllers/{id}", testController.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("테스트2번")));

        Controller updatedController = controllerRepository.findById(testController.getId()).orElseThrow();
        assertThat(updatedController.getName()).isEqualTo("테스트2번");
    }

    @DisplayName("MANAGER가 컨트롤러 정보를 수정할 수 있다")
    @Test
    void updateController_Manager_Success() throws Exception {
        // Given
        setAuthentication(manager);
        UpdateControllerRequest request = new UpdateControllerRequest(
                "테스트2번",
                "20",
                "25",
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
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/controllers/{id}", testController.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("테스트2번")));

        Controller updatedController = controllerRepository.findById(testController.getId()).orElseThrow();
        assertThat(updatedController.getName()).isEqualTo("테스트2번");
    }

    @DisplayName("일반 USER가 컨트롤러 정보를 수정할 수 있다")
    @Test
    void updateController_User_Fail() throws Exception {
        // Given
        setAuthentication(user);
        UpdateControllerRequest request = new UpdateControllerRequest(
                "테스트2번",
                "20",
                "25",
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
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/controllers/{id}", testController.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("테스트2번")));

        Controller updatedController = controllerRepository.findById(testController.getId()).orElseThrow();
        assertThat(updatedController.getName()).isEqualTo("테스트2번");
    }

    @DisplayName("ADMIN이 컨트롤러를 삭제할 수 있다")
    @Test
    void deleteController_Admin_Success() throws Exception {
        // Given
        setAuthentication(admin);

        // When
        ResultActions result = mockMvc.perform(delete("/api/controllers/{id}", testController.getId()));

        // Then
        result.andExpect(status().isOk());
        assertThat(controllerRepository.findById(testController.getId())).isEmpty();
    }

    @DisplayName("MANAGER가 컨트롤러를 삭제할 수 있다")
    @Test
    void deleteController_Manager_Success() throws Exception {
        // Given
        setAuthentication(manager);

        // When
        ResultActions result = mockMvc.perform(delete("/api/controllers/{id}", testController.getId()));

        // Then
        result.andExpect(status().isOk());
        assertThat(controllerRepository.findById(testController.getId())).isEmpty();
    }

    @DisplayName("일반 USER가 컨트롤러를 삭제하려고 하면 실패한다")
    @Test
    void deleteController_User_Fail() throws Exception {
        // Given
        setAuthentication(user);

        // When
        ResultActions result = mockMvc.perform(delete("/api/controllers/{id}", testController.getId()));

        // Then
        result.andExpect(status().isForbidden());
        assertThat(controllerRepository.findById(testController.getId())).isPresent();
    }
    @AfterEach
    public void end() {
        controllerRepository.deleteAll();
        userRepository.deleteAll();
        groupRepository.deleteAll();
    }
}