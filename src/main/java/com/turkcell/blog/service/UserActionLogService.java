package com.turkcell.blog.service;

import com.turkcell.blog.entity.UserActionLog;

import java.util.List;

public interface UserActionLogService {
    void logAction(String username, String action);
    List<UserActionLog> getAllLogs();
}
