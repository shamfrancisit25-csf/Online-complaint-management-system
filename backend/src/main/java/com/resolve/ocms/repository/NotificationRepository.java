package com.resolve.ocms.repository;
import com.resolve.ocms.model.AppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
public interface NotificationRepository extends JpaRepository<AppNotification,Long> {}
