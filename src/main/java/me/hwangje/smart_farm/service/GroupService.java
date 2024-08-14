package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.domain.User;
import me.hwangje.smart_farm.dto.GroupDto.*;
import me.hwangje.smart_farm.repository.GroupRepository;
import me.hwangje.smart_farm.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    // Create
    @Transactional
    public Group save(AddGroupRequest request) {
        Group group = Group.builder()
                .name(request.getName())
                .contact(request.getContact())
                .registrationNumber(request.getRegistrationNumber())
                .build();
        return groupRepository.save(group);
    }

    // Read (단일 그룹)
    public Group findById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + id));
    }

    // Read (모든 그룹)
    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    // Read
    public List<Group> searchGroups(String name){
        return groupRepository.findByNameContaining(name);
    }

    // Update
    @Transactional
    public Group update(Long id, UpdateGroupRequest request) {
        Group group = findById(id);
        group.update(request.getName(), request.getContact(), request.getRegistrationNumber());
        return group;
    }

    // Delete
    @Transactional
    public void delete(Long id) {
        Group group = findById(id);

        // 그룹에 속한 사용자들의 그룹 참조를 null로 설정
        group.removeAllUsers();

        // 그룹 삭제
        groupRepository.delete(group);
    }
}