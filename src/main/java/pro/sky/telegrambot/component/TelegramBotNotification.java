package pro.sky.telegrambot.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotSender;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TelegramBotNotification {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final Logger logger = LoggerFactory.getLogger(TelegramBotNotification.class);
    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBotSender telegramBotSender;

    public TelegramBotNotification(NotificationTaskRepository notificationTaskRepository, TelegramBotSender telegramBotSender) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBotSender = telegramBotSender;
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendNotifications() {
        LocalDateTime currentDateTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        logger.info("Was invoked method to check notification tasks for {}", currentDateTime.format(dateTimeFormatter));
        List<NotificationTask> notificationTasks = notificationTaskRepository.findAllByDateTime(currentDateTime);
        logger.info("Found {} tasks", notificationTasks.size());
        notificationTasks.forEach(notificationTask -> {
            telegramBotSender.sendMessage(notificationTask.getChatId(), "Напоминание! " + notificationTask.getMessageText());
            notificationTaskRepository.delete(notificationTask);
        });
    }
}
