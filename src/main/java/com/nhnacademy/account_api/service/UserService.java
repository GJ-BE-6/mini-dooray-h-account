package com.nhnacademy.account_api.service;


import com.nhnacademy.account_api.domain.entity.UserStatus;
import com.nhnacademy.account_api.domain.entity.User;
import com.nhnacademy.account_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User createUser(User user) {
        Optional<User> existingUser = userRepository.findByUserEmail(user.getUserEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 Email 입니다.");
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<User> getUsersByUserName(String userName, Pageable pageable) {
        return userRepository.findByUserName(userName, pageable);
    }

    @Transactional(readOnly = true)
    public Optional<User> getUserByUserEmail(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public boolean existsByUserId(String userId) {
        return userRepository.existsById(userId);
    }

    public void updateDormantStatus() {
        LocalDateTime tenSecondsAgo = LocalDateTime.now().minusSeconds(10);
        int page = 0;
        int size = 10;

        Page<User> userPage;
        do {
            PageRequest pageRequest = PageRequest.of(page, size);
            userPage = userRepository.findByLastLoginDateBeforeAndUserStatus(tenSecondsAgo, UserStatus.ACTIVE, pageRequest);

            for (User user : userPage) {
                if (user.getLastLoginDate() == null) {
                    continue;
                }
                user.setUserStatus(UserStatus.DORMANT);
                userRepository.save(user);
            }
            page++;
        } while (userPage.hasNext());
    }

    public void markAsDeleted(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User를 찾을 수 없습니다."));
        user.setUserStatus(UserStatus.DELETED);
        userRepository.save(user);
    }

    public void updateUser(String userId, User updatedUser) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User를 찾을 수 없습니다."));
        user.setUserName(updatedUser.getUserName());
        user.setUserEmail(updatedUser.getUserEmail());
        user.setUserPassword(updatedUser.getUserPassword());
        user.setUserStatus(updatedUser.getUserStatus());
        userRepository.save(user);
    }

    public void deleteUserPermanently(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User를 찾을 수 없습니다."));
        userRepository.delete(user);
    }

    public void authenticateUser(String userId, String password) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User를 찾을 수 없습니다."));
        if (!user.getUserPassword().equals(password)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        if (user.getUserStatus() == UserStatus.DELETED) {
            throw new RuntimeException("탈퇴한 사용자입니다.");
        }
        // 최근 접속일 업데이트
        user.setUserStatus(UserStatus.ACTIVE);
        user.setLastLoginDate(LocalDateTime.now());
        userRepository.save(user);
    }

}
