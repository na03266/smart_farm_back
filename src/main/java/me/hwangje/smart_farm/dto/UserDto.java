package me.hwangje.smart_farm.dto;

import lombok.Getter;
import lombok.Setter;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;

public class UserDto {
    @Getter
    @Setter
    public static class AddUserRequest {
        private String email;
        private String password;
        private String name;
        private String contact;
        private String manager;
        private Group group;
        private Role role;
    }

}
