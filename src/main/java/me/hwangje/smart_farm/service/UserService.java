package me.hwangje.smart_farm.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.Role;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.UserDto.*;
import me.hwangje.smart_farm.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public User save(AddUserRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        return userRepository.save(User.builder()
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .name(request.getName())
                .role(request.getRole())
                .contact(request.getContact())
                .manager(request.getManager())
                .group(request.getGroup())
                .build());
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Transactional
    public User update(Long id, UpdateUserRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        User user = findById(id);
        user.update(request.getName(),
                request.getContact(),
                request.getPassword() != null ? encoder.encode(request.getPassword()) : user.getPassword(),
                request.getRole(),
                request.getGroup(),
                request.getManager());
        return user;
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByEmail(email);
    }

    @Transactional
    public User updateCurrentUser(UpdateUserRequest request) {
        User currentUser = getCurrentUser();
        return update(currentUser.getId(), request);
    }


    @Transactional
    public void deleteCurrentUser() {
        User currentUser = getCurrentUser();
        delete(currentUser.getId());
    }

    public List<User> findUsersByCriteriaAndRole(String nameLike, String phoneLike, String managerNameLike)
            throws AccessDeniedException {
        User currentUser = getCurrentUser();
        if (currentUser.getRole().equals(Role.ADMIN)) {
            return findUsersWithCriteria(nameLike, phoneLike, managerNameLike);
        } else if (currentUser.getRole().equals(Role.MANAGER)) {
            return findUsersInSameGroupWithCriteria(currentUser.getGroup(), nameLike, phoneLike, managerNameLike);
        } else {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }
    }

    public List<User> findUsersWithCriteria(String nameLike, String phoneLike, String managerNameLike) {
        return userRepository.findByCriteria(nameLike, phoneLike, managerNameLike);

    }

    public List<User> findUsersInSameGroupWithCriteria(Group group, String nameLike, String phoneLike, String managerNameLike) {
        return userRepository.findByGroupAndCriteria(group, nameLike, phoneLike, managerNameLike);

    }
}