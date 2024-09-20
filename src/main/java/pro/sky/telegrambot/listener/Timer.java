package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.MessageService;
import pro.sky.telegrambot.model.Message;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;

@Service
public class Timer {
    private final MessageService messageService;
    private final Logger logger = LoggerFactory.getLogger(Timer.class);
    private final TelegramBot telegramBot;

    public Timer(MessageService messageService, TelegramBot telegramBot) {
        this.messageService = messageService;
        this.telegramBot = telegramBot;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    void run() {
        List<Message> messages = messageService.findMessage(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        messages.forEach(task -> {
            SendMessage message = new SendMessage(task.getChatId(), task.getTextMessage());
            telegramBot.execute(message);
            logger.info("Отправлено уведомление: chatId = " + task.getChatId() + " , text = " + task.getTextMessage());
            messageService.delete(task.getId());
            logger.info("Удалено уведомление из БД");
        });
    }

}
