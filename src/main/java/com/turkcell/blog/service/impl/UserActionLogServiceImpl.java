package com.turkcell.blog.service.impl;


import com.turkcell.blog.entity.UserActionLog;
import com.turkcell.blog.repository.UserActionLogRepository;
import com.turkcell.blog.service.UserActionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserActionLogServiceImpl implements UserActionLogService {

    private final UserActionLogRepository logRepository;

    @Override
    public void logAction(String username, String action) {
        UserActionLog log = UserActionLog.builder()
                .username(username)
                .action(action)
                .timestamp(LocalDateTime.now())
                .build();
        logRepository.save(log);
    }

    @Override
    public List<UserActionLog> getAllLogs() {
        return logRepository.findAllByOrderByTimestampDesc();
    }
}

