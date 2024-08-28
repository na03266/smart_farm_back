package me.hwangje.smart_farm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.Controller;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.ControllerDto;
import me.hwangje.smart_farm.repository.ControllerRepository;
import me.hwangje.smart_farm.repository.DeviceSetupRepository;
import me.hwangje.smart_farm.repository.GroupRepository;
import me.hwangje.smart_farm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MqttPublishServiceTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    DeviceSetupRepository deviceSetupRepository;

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

    @MockBean
    MqttService mqttService;

    User admin;
    User user;
    Controller testController;
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

    @DisplayName("publishSetup 메서드가 올바르게 MQTT 메시지를 발행한다")
    @Test
    void publishSetup_PublishesCorrectMessage() throws Exception {
        // Given
        testController = createController(request);
        ControllerDto.UpdateControllerRequest request = new ControllerDto.UpdateControllerRequest(
                "테스트2번",
                "[20]",
                "[25, 20]",
                1,
                30,
                8,
                1,
                28,
                18,
                "[0,1,0,1,2,3,4,5,6,7,8]",
                1,
                user.getId()
        );
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/controllers/{controllerId}", testController.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.iceType", is(8)));
        // MQTT 메시지 발행 검증
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(mqttService).publishMessage(topicCaptor.capture(), messageCaptor.capture());

    }
}