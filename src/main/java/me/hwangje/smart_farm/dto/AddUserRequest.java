package me.hwangje.smart_farm.dto;

import lombok.Getter;
import lombok.Setter;
import me.hwangje.smart_farm.domain.Article;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;

@Getter
@Setter
public class AddUserRequest {
    private String email;
    private String password;
    private Role role;
    private String userName;
    private String phoneNumber;
    private String manager;
    private String company;
}
