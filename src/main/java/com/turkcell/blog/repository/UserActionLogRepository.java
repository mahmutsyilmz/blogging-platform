package com.turkcell.blog.repository;

import com.turkcell.blog.entity.UserActionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActionLogRepository extends JpaRepository<UserActionLog, Long> {

    List<UserActionLog> findAllByOrderByTimestampDesc();
}
