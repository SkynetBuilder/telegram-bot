package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.telegrambot.entity.NotificationTask;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface NotificationTaskRepository extends JpaRepository<NotificationTask, UUID> {
    List<NotificationTask> findAllByDateTime(LocalDateTime dateTime);
}
