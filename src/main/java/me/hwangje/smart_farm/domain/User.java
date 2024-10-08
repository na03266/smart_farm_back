package me.hwangje.smart_farm.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "manager")
    private String manager;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;

    @Getter
    @Column(name = "name")
    private String name;

    @Column(name = "contact")
    private String contact;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Controller> controllers = new ArrayList<>();

    @Override // 권한 반환
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Builder
    public User(String email, String password, Role role, String name, String contact, Group group, String manager) {
        this.email = email;
        this.password = password;
        this.role = (role!=null)? role : Role.USER;
        this.name = name;
        this.contact = contact;
        this.manager = manager;
        this.group = group;
    }

    // 사용자 id 반환
    @Override
    public String getUsername() {
        return email;
    }

    //사용자 패스워드 반환
    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        // 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금되었는지 확인하는 로직
        return true; // true -> 잠금되지 않았음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 패스워드가 만료되었는지 확인하는 로직
        return true; // true -> 만료되지 않았음
    }

    @Override
    public boolean isEnabled() {
        // 계정이 사용 가능한지 확인하는 로직
        return true; // true -> 사용 가능
    }

    // 사용자 이름 변경
    public User update(String name, String contact, String password, Role role, Group group, String manager) {
        if (name != null) {
            this.name = name;
        }
        if (contact != null) {
            this.contact = contact;
        }
        if (password != null) {
            this.password = password;
        }
        if (role != null) {
            this.role = role;
        }
        if(group != null){
            this.group = group;
        }
        if (manager != null){
            this.manager = manager;
        }
        return this;
    }

    public void setGroup(Group group) {
        // 기존 그룹과의 관계를 제거
        if (this.group != null) {
            this.group.getUsers().remove(this);
        }
        this.group = group;
        // 새 그룹과의 관계를 설정
        if (group != null) {
            group.getUsers().add(this);
        }
    }

    public void addController(Controller controller) {
        controllers.add(controller);
        controller.setUser(this);
    }

    public void removeController(Controller controller) {
        controllers.remove(controller);
        controller.setUser(null);
    }
}
