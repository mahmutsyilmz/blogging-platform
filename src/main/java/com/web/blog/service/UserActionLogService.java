package com.web.blog.service;

import com.web.blog.entity.UserActionLog;

import java.util.List;

public interface UserActionLogService {
    void logAction(String username, String action);
    List<UserActionLog> getAllLogs();
}
