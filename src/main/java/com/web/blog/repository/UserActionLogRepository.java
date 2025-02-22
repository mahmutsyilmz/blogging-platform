package com.web.blog.repository;

import com.web.blog.entity.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {

    List<UserActionLog> findAllByOrderByTimestampDesc();
}
