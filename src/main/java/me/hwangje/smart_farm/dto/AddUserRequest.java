package me.hwangje.smart_farm.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddUserRequest {
    private String userName;
    private String password;
}
