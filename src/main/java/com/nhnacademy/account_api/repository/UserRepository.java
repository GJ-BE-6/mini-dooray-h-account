package com.nhnacademy.account_api.repository;

import com.nhnacademy.account_api.domain.entity.UserStatus;
import com.nhnacademy.account_api.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Page<User> findByUserName(String userName, Pageable pageable);
    Optional<User> findByUserEmail(String userEmail);
    Page<User> findByLastLoginDateBeforeAndUserStatus(LocalDateTime dateTime, UserStatus status, Pageable pageable);

}
