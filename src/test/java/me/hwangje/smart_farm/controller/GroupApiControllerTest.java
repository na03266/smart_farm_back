package me.hwangje.smart_farm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.GroupDto.*;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.hamcrest.Matchers.is;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GroupApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    UserRepository userRepository;

    User admin;
    User manager;
    User user;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
        .build();
        groupRepository.deleteAll();
        userRepository.deleteAll();

        admin = createUser("admin@gmail.com", Role.ADMIN);
        manager = createUser("manager@gmail.com", Role.MANAGER);
        user = createUser("user@gmail.com", Role.USER);
    }

    private User createUser(String email, Role role) {
        return userRepository.save(User.builder()
                .email(email)
                .password("test")
                .role(role)
                .name(role.name())
                .contact("01012345678")
                .build());
    }

    private void setAuthentication(User user) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
    }

    @DisplayName("관리자로 그룹 추가에 성공한다")
    @Test
    void addGroup_AsAdmin_Success() throws Exception {
        // Given
        setAuthentication(admin);
        AddGroupRequest request = new AddGroupRequest("New Group", "01022222222", "9876543210");
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Group")));

        List<Group> groups = groupRepository.findAll();
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0).getName()).isEqualTo("New Group");
    }

    @DisplayName("매니저로 그룹 추가 시 실패한다")
    @Test
    void addGroup_AsManager_Fail() throws Exception {
        // Given
        setAuthentication(manager);
        AddGroupRequest request = new AddGroupRequest("New Group", "01022222222", "9876543210");
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(post("/api/groups")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isForbidden());

        List<Group> groups = groupRepository.findAll();
        assertThat(groups).isEmpty();
    }

    @DisplayName("모든 그룹을 조회한다")
    @Test
    void findAllGroups_Success() throws Exception {
        // Given
        Group group = createDefaultGroup();
        setAuthentication(admin);

        // When
        ResultActions result = mockMvc.perform(get("/api/groups")
                .accept(MediaType.APPLICATION_JSON));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is(group.getName())));
    }

    @DisplayName("특정 그룹을 조회한다")
    @Test
    void findGroup_Success() throws Exception {
        // Given
        Group group = createDefaultGroup();
        setAuthentication(user);

        // When
        ResultActions result = mockMvc.perform(get("/api/groups/{id}", group.getId()));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(group.getName())));
    }

    @DisplayName("그룹 정보를 수정한다")
    @Test
    void updateGroup_Success() throws Exception {
        // Given
        Group group = createDefaultGroup();
        setAuthentication(manager);
        UpdateGroupRequest request = new UpdateGroupRequest("Updated Group", "01033333333", "1122334455");
        String requestBody = objectMapper.writeValueAsString(request);

        // When
        ResultActions result = mockMvc.perform(put("/api/groups/{id}", group.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody));

        // Then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Group")));

        Group updatedGroup = groupRepository.findById(group.getId()).orElseThrow();
        assertThat(updatedGroup.getName()).isEqualTo("Updated Group");
    }

    @DisplayName("그룹을 삭제한다")
    @Test
    void deleteGroup_Success() throws Exception {
        // Given
        Group group = createDefaultGroup();
        setAuthentication(admin);

        // When
        ResultActions result = mockMvc.perform(delete("/api/groups/{id}", group.getId()));

        // Then
        result.andExpect(status().isOk());

        assertThat(groupRepository.findById(group.getId())).isEmpty();
    }

    private Group createDefaultGroup() {
        return groupRepository.save(Group.builder()
                .name("Default Group")
                .contact("01011111111")
                .registrationNumber("0123456789")
                .build());
    }
}