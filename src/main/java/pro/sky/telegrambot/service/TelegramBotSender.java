package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotSender {
    private final Logger logger = LoggerFactory.getLogger(TelegramBotSender.class);
    private final TelegramBot telegramBot;

    public TelegramBotSender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    public void sendMessage(Long chatId, String message) {
        logger.info("Was invoked method to send message");
        SendMessage sendMessage = new SendMessage(chatId, message);
        SendResponse sendResponse = telegramBot.execute(sendMessage);
        if (sendResponse.isOk()) {
            logger.info("Message is sent: {}", message);
        } else {
            logger.error("Didn't send message, an error occurred: {}", sendResponse.errorCode());
        }
    }
}
