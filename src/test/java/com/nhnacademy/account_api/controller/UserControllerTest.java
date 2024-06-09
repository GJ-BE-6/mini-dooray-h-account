package com.nhnacademy.account_api.controller;

import com.nhnacademy.account_api.domain.entity.User;
import com.nhnacademy.account_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    public void testRegisterUser_UserExists() throws Exception {
        when(userService.existsByUserId(anyString())).thenReturn(true);

        mockMvc.perform(post("/api/account/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"existingUser\", \"userName\": \"testUser\", \"email\": \"test@example.com\", \"password\": \"password\"}"))
                .andExpect(status().isConflict())
                .andExpect(content().string("User ID가 이미 존재합니다."));
    }

    @Test
    public void testGetUserById_Success() throws Exception {
        User user = new User();
        user.setUserId("existingUser");

        when(userService.getUserById(anyString())).thenReturn(user);

        mockMvc.perform(get("/api/account/existingUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("existingUser"));
    }

    @Test
    public void testUpdateUser_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setUserId("existingUser");
        updatedUser.setUserName("updatedUser");
        updatedUser.setUserEmail("updated@example.com");
        updatedUser.setUserPassword("newPassword");

        doNothing().when(userService).updateUser(anyString(), any(User.class));

        mockMvc.perform(put("/api/account/existingUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": \"existingUser\", \"userName\": \"updatedUser\", \"email\": \"updated@example.com\", \"password\": \"newPassword\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("User 업데이트 성공"));
    }

    @Test
    public void testDeleteUserPermanently_Success() throws Exception {
        doNothing().when(userService).deleteUserPermanently(anyString());

        mockMvc.perform(delete("/api/account/permanent/existingUser"))
                .andExpect(status().isOk())
                .andExpect(content().string("User 영구 삭제 성공"));
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        doNothing().when(userService).authenticateUser(anyString(), anyString());

        mockMvc.perform(post("/api/account/authenticate")
                        .param("userId", "existingUser")
                        .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string("User 인증 성공"));
    }

    @Test
    public void testGetUserByEmail_Success() throws Exception {
        User user = new User();
        user.setUserEmail("test@example.com");

        when(userService.getUserByUserEmail(anyString())).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/account/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userEmail").value("test@example.com"));
    }

}
