package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.TelegramBotSender;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBotSender telegramBotSender;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository, TelegramBotSender telegramBotSender) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBotSender = telegramBotSender;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            Long chatId = update.message().chat().id();
            String message = update.message().text();
            if (message.matches("/start")) {
                telegramBotSender.sendMessage(chatId, "Привет! Здесь можно сохранить напоминание в формате ДД.ММ.ГГГГ часы:минуты <текст_напоминания_без_цифр>");
            } else {
                Matcher matcher = MESSAGE_PATTERN.matcher(message);
                if (matcher.matches()) {
                    LocalDateTime dateTime = LocalDateTime.parse(matcher.group(1), dateTimeFormatter);
                    if (dateTime.isBefore(LocalDateTime.now())){
                        telegramBotSender.sendMessage(chatId,"Указана прошедшая дата и/или время, укажите повторно");
                        logger.error("Past time or/and date was inputted");
                    } else {
                        String messageText = matcher.group(3);
                        NotificationTask notificationTask = new NotificationTask(chatId, messageText, dateTime);
                        notificationTaskRepository.save(notificationTask);
                        telegramBotSender.sendMessage(chatId,
                                "Напоминание сохранено. Дата и время: " + dateTime.format(dateTimeFormatter) + ", текст: " + messageText);
                    }
                }
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }
}
