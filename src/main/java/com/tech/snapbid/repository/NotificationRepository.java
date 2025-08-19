package com.tech.snapbid.repository;

import com.tech.snapbid.models.Notification;
import com.tech.snapbid.models.User;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUserAndReadAtIsNull(User user);

    @Modifying
    @Query("update Notification n set n.readAt = :ts where n.user.id = :userId and n.readAt is null")
    int markAllReadByUserId(@Param("userId") Long userId, @Param("ts") LocalDateTime ts);
}