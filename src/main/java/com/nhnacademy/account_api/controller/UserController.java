package com.nhnacademy.account_api.controller;

import com.nhnacademy.account_api.domain.entity.User;
import com.nhnacademy.account_api.domain.entity.UserStatus;
import com.nhnacademy.account_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
@Tag(name = "Account API", description = "Account API(회원정보관리) 입니다.")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    @Operation(summary = "User 생성", description = "User 정보를 생성합니다.")
    public ResponseEntity<String> registerUser(@Valid @RequestBody User user) {
        if (userService.existsByUserId(user.getUserId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("User ID가 이미 존재합니다.");
        }

        try {
            user.setUserStatus(UserStatus.ACTIVE);
            userService.createUser(user);
            return ResponseEntity.ok("User 생성 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User 생성 실패: " + e.getMessage());
        }
    }

    @GetMapping("/{userId}")
    @Operation(summary = "User 조회", description = "User ID로 User 정보를 조회합니다.")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        try {
            User user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/email")
    @Operation(summary = "User 이메일로 조회", description = "User 이메일로 User 정보를 조회합니다.")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        Optional<User> user = userService.getUserByUserEmail(email);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @GetMapping("/name")
    @Operation(summary = "User 이름으로 조회", description = "User 이름으로 User 정보를 조회합니다.")
    public ResponseEntity<Page<User>> getUserByName(@RequestParam String name,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> user = userService.getUsersByUserName(name, pageable);
        if (user.hasContent()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/all")
    @Operation(summary = "User 모두 조회", description = "모든 User 정보를 조회합니다.")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> user = userService.getAllUsers(pageable);
        if (user.hasContent()) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PutMapping("/{userId}")
    @Operation(summary = "User 업데이트", description = "User ID로 User 정보를 업데이트합니다.")
    public ResponseEntity<String> updateUser(@PathVariable String userId, @Valid @RequestBody User updatedUser) {
        try {
            userService.updateUser(userId, updatedUser);
            return ResponseEntity.ok("User 업데이트 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User 업데이트 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "User 탈퇴 처리", description = "User ID로 User를 탈퇴 처리합니다.")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        try {
            userService.markAsDeleted(userId);
            return ResponseEntity.ok("User 탈퇴 처리 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User 탈퇴 처리 실패: " + e.getMessage());
        }
    }

    @DeleteMapping("/permanent/{userId}")
    @Operation(summary = "User 영구 삭제", description = "User ID로 User를 영구 삭제합니다.")
    public ResponseEntity<String> deleteUserPermanently(@PathVariable String userId) {
        try {
            userService.deleteUserPermanently(userId);
            return ResponseEntity.ok("User 영구 삭제 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User 영구 삭제 실패: " + e.getMessage());
        }
    }

    @PostMapping("/authenticate")
    @Operation(summary = "User 인증", description = "User ID와 비밀번호로 인증합니다.")
    public ResponseEntity<String> authenticateUser(@RequestParam String userId, @RequestParam String password) {
        try {
            userService.authenticateUser(userId, password);
            return ResponseEntity.ok("User 인증 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User 인증 실패: " + e.getMessage());
        }
    }

}
