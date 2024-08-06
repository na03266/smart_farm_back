package me.hwangje.smart_farm.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "groups")
@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact", nullable = false)
    private String contact;

    @Column(name = "registration_number", nullable = false)
    private String registrationNumber;


    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Group(String name, String contact, String registrationNumber) {
        this.name = name;
        this.contact = contact;
        this.registrationNumber = registrationNumber;
    }

    public void update(String name, String contact, String registrationNumber) {
        this.name = name;
        this.contact = contact;
        this.registrationNumber = registrationNumber;
    }

    public void addUser(User user) {
        this.users.add(user);
        user.setGroup(this);
    }

    public void removeUser(User user) {
        this.users.remove(user);
        user.setGroup(null);
    }
}
