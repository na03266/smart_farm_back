package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "groups")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @JsonBackReference
    @OneToMany(mappedBy = "group", orphanRemoval = true)
    private List<User> users = new ArrayList<>();

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
        if (!this.users.contains(user)) {
            this.users.add(user);
            user.setGroup(this);
        }
    }

    public void removeUser(User user) {
        if (this.users.contains(user)) {
            this.users.remove(user);
            user.setGroup(null);
        }
    }

    public void removeAllUsers() {
        for (User user : new ArrayList<>(users)) {
            user.setGroup(null);
        }
        this.users.clear();
    }
}
