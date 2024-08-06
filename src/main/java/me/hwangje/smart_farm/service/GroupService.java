package me.hwangje.smart_farm.service;

import lombok.RequiredArgsConstructor;
import me.hwangje.smart_farm.domain.Group;
import me.hwangje.smart_farm.dto.GroupDto.*;
import me.hwangje.smart_farm.repository.GroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {
    private final GroupRepository groupRepository;

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
        groupRepository.delete(group);
    }
}