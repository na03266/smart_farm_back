package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.UserDto.AddUserRequest;
import me.hwangje.smart_farm.dto.UserDto.UpdateUserRequest;
import me.hwangje.smart_farm.repository.GroupRepository;
import me.hwangje.smart_farm.repository.UserRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GroupRepository groupRepository;
    User admin;
    User manager;
    User user;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userRepository.deleteAll();
        Group newGroup = groupRepository.save(Group.builder()
                .name("New Group")
                .contact("01099999999")
                .registrationNumber("9876543210")
                .build());
        admin = createUser("admin@gmail.com", null, Role.ADMIN);
        manager = createUser("manager@gmail.com", newGroup, Role.MANAGER);
        user = createUser("user@gmail.com", newGroup, Role.USER);
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

    @DisplayName("회원가입에 성공한다")
    @Test
    void 회원가입() throws Exception {
        // Given
        AddUserRequest request = AddUserRequest.builder()
                .email("example@email.com")
                .password("password123")
                .name("John Doe")
                .contact("01012345678")
                .manager("Manager Name")
                .role(Role.USER)
                .build();
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(post("/api/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is("example@email.com")));

        assertThat(userRepository.findByEmail("example@email.com")).isPresent();
    }

    @DisplayName("로그아웃에 성공한다")
    @Test
    void 로그아웃() throws Exception {
        // Given
        setAuthentication(user);

        // When
        ResultActions result = mockMvc.perform(post("/api/users/logout"));

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        result.andExpect(status().isOk());
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @DisplayName("현재 사용자 정보를 가져온다")
    @Test
    void 사용자정보_본인() throws Exception {
        // Given
        setAuthentication(user);

        // When
        ResultActions result = mockMvc.perform(get("/api/users/me"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(user.getEmail())));
    }

    @DisplayName("사용자 정보를 업데이트한다")
    @Test
    void 사용자정보수정_본인() throws Exception {
        // Given
        setAuthentication(user);


        UpdateUserRequest request = new UpdateUserRequest("newpassword", "Updated Name", "01088888888", "두통", user.getGroup(), Role.USER);
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));

        User updatedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
    }

    @DisplayName("관리자가 사용자 정보를 업데이트한다")
    @Test
    void 사용자정보수정_관리자() throws Exception {
        // Given
        setAuthentication(admin);
        Group newGroup = groupRepository.save(Group.builder()
                .name("New Group")
                .contact("01099999999")
                .registrationNumber("9876543210")
                .build());

        UpdateUserRequest request = new UpdateUserRequest("newpassword", "Updated Name", "01088888888", "", newGroup, Role.USER);
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.role", is("USER")));

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("매니저가 사용자 정보를 업데이트한다")
    @Test
    void 사용자정보수정_매니저() throws Exception {
        // Given
        setAuthentication(manager);
        Group newGroup = groupRepository.save(Group.builder()
                .name("New Group")
                .contact("01099999999")
                .registrationNumber("9876543210")
                .build());

        UpdateUserRequest request = new UpdateUserRequest("newpassword", "Updated Name", "01088888888", "", newGroup, Role.USER);
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.role", is("USER")));

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getRole()).isEqualTo(Role.USER);
    }

    @DisplayName("사용자를 삭제한다")
    @Test
    void 사용자계정_삭제_관리자() throws Exception {
        // Given
        setAuthentication(admin);

        // When
        ResultActions result = mockMvc.perform(delete("/api/users/{id}", user.getId()));

        // Then
        result.andExpect(status().isNoContent());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @DisplayName("관리자가 조건에 해당하는 모든 사용자를 조회한다")
    @Test
    void 조건부_사용자조회_관리자() throws Exception {
        // Given
        setAuthentication(admin);

        // When
        ResultActions result = mockMvc.perform(get("/api/users"));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].email", containsInAnyOrder(admin.getEmail(), manager.getEmail(), user.getEmail())));
    }

    @DisplayName("매니저가 조건에 해당하는 같은 그룹의 사용자와 매니저를 조회한다")
    @Test
    void 조건부_사용자조회_매니저() throws Exception {
        // Given
        setAuthentication(manager);


        // When
        MvcResult mvcResult = mockMvc.perform(get("/api/users")
                        .param("nameLike", "")
                        .param("phoneLike", "")
                        .param("managerNameLike", ""))
                .andExpect(status().isOk())
                .andReturn();

        // Then
        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);


    }

    @DisplayName("검색 조건을 적용하여 사용자를 조회한다")
    @Test
    void 조건부_사용자조회_검색조건() throws Exception {
        // Given
        setAuthentication(admin);
        String nameLike = "test";

        // When
        ResultActions result = mockMvc.perform(get("/api/users")
                .param("nameLike", nameLike)
                .param("phoneLike", "")
                .param("managerNameLike", ""));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", everyItem(containsString(nameLike))));
    }
}