package me.hwangje.smart_farm.controller;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.dto.AddUserRequest;
import me.hwangje.smart_farm.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserApiController {
    private final UserService userService;

    @PostMapping("user")
    public String signup(AddUserRequest request){
        userService.sava(request);

        return "redirect:/login";
    }
}
