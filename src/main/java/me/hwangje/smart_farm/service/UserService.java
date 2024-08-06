package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Role;
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

    public Long save(AddUserRequest dto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(dto.getEmail())
                //패스워드 암호화
                .password(encoder.encode(dto.getPassword()))
                .nickname(dto.getNickname())
                .phoneNumber(dto.getPhoneNumber())
                .role(dto.getRole())
                .build()).getId();
    }

    public List<User> findByGroup(Long id){
        return userRepository.findByGroup(id);
    }
    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    /**
     * 모든 사용자 정보를 조회
     *
     * @return 모든 사용자 정보 목록
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public List<User> findByUserNameContaining(String nameFragment) {
        return userRepository.findByNicknameContaining(nameFragment);
    }

    public List<User> findByGroup(String group) {
        return userRepository.findByRole(role);
    }

    public List<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.findByEmailContainingOrNicknameContaining(keyword, keyword);
    }

    /**
     * 사용자 삭제, ADMIN 인 경우 아무나 삭제 가능, MANAGER인 경우 같은 소속의 매니저만 삭제 가능
     * @param id
     */
    public void delete(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found " + id));

        if(user.getRole() == Role.ADMIN) {
            userRepository.deleteById(id);
        }
    }

}

