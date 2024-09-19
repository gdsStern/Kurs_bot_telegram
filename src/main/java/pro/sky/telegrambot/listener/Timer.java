package pro.sky.telegrambot.listener;

import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.MessageService;
import pro.sky.telegrambot.model.Message;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;

@Service
public class Timer {

    private final MessageService messageService;
    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);
    private final TelegramBotUpdatesListener telegramBotUpdatesListener;

    public Timer(MessageService messageService, TelegramBotUpdatesListener telegramBotUpdatesListener) {
        this.messageService = messageService;
        this.telegramBotUpdatesListener = telegramBotUpdatesListener;
    }
    @Scheduled(cron = "0 0/1 * * * *")
    void run() {
        List<Message> messages = messageService.findMessage(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        messages.forEach(task -> {
            logger.info("Отправлено уведомление: chatId = " + task.getChatId() + " , text = " + task.getText());
            telegramBotUpdatesListener.sendMessage(task.getChatId(), task.getText());
            messageService.delete(task.getId());
            logger.info("Удалено уведомление из БД");
        });
        messages = messageService.findIsAfterMessage();
        messages.forEach(task -> {
            telegramBotUpdatesListener.sendMessage(task.getChatId(), task.getData() + " " + task.getText());
            logger.info("Отправлено прошедшее уведомление: chatId = " + task.getChatId() + " , text = " + task.getText());
            messageService.delete(task.getId());
            logger.info("Удалено уведомление из БД");
        });
    }
}
