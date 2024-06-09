package com.nhnacademy.account_api.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user", uniqueConstraints = @UniqueConstraint(columnNames = "user_email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @Column(name = "user_id", nullable = false, length = 20)
    @NotBlank(message = "사용자 ID는 필수입니다.")
    private String userId;

    @Column(name = "user_name", nullable = false, length = 20)
    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String userName;

    @Column(name = "user_email", nullable = false, length = 50, unique = true)
    @Email(message = "올바른 이메일 주소여야 합니다.")
    private String userEmail;

    @Column(name = "user_password", nullable = false, length = 255)
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    private String userPassword;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    // 생성자: lastLoginDate 제외한 모든 필드를 포함
    public User(String userId, String userName, String userEmail, String userPassword) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }
}
