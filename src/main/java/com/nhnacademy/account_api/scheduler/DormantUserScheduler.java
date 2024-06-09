package com.nhnacademy.account_api.scheduler;


import com.nhnacademy.account_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DormantUserScheduler {

    private final UserService usersService;

    @Scheduled(cron = "*/10 * * * * *") // 매 10초마다 실행
    public void updateDormantUsers() {
        usersService.updateDormantStatus();
    }
}
