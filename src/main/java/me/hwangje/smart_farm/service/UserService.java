package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.AddUserRequest;
import me.hwangje.smart_farm.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest dto) {
        return userRepository.save(User.builder()
                .email(dto.getEmail())
                //패스워드 암호화
                .password(bCryptPasswordEncoder.encode(dto.getPassword()))
                .userName(dto.getUserName())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole())
                .build()).getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    /**
     * 모든 사용자 정보를 조회
     * @return 모든 사용자 정보 목록
     */
    public List<User> findAll(){
        return userRepository.findAll();
    }
    public User findById(long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found " + id));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByUserNameContaining(String nameFragment) {
        return userRepository.findByUserNameContaining(nameFragment);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public List<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByEmailContainingOrUserNameContaining(keyword, keyword);
    }

    public void delete(long id){
        userRepository.deleteById(id);
    }

}

