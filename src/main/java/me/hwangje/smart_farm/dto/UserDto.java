package me.hwangje.smart_farm.dto;

import lombok.*;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;

public class UserDto {
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class AddUserRequest {
        private String email;
        private String password;
        private String name;
        private String contact;
        private String manager;
        private Group group;
        private Role role;

        public User toEntity() {
            return User.builder()
                    .email(email)
                    .password(password)
                    .name(name)
                    .contact(contact)
                    .manager(manager)
                    .group(group)
                    .role(role)
                    .build();
        }
    }

    @Getter
    public static class UserResponse {
        private String email;
        private String password;
        private String name;
        private String contact;
        private String manager;
        private Group group;
        private Role role;

        public UserResponse(User user) {
            this.email = user.getEmail();
            this.password = user.getPassword();
            this.name = user.getName();
            this.contact = user.getContact();
            this.manager = user.getManager();
            this.group = user.getGroup();
            this.role = user.getRole();
        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class UpdateUserRequest {
        private String password;
        private String name;
        private String contact;
        private String manager;
        private Group group;
        private Role role;

    }
}
