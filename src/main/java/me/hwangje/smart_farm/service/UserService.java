package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.UserDto.*;
import me.hwangje.smart_farm.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Long save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                .password(encoder.encode(dto.getPassword()))
                .name(dto.getName())
                .role(dto.getRole())
                .contact(dto.getContact())
                .managerName(dto.getManager())
                .group(dto.getGroup())
                .build()).getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}