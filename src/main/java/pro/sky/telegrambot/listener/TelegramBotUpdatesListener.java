package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Message;
import pro.sky.telegrambot.service.MessageService;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private String startMessage = "Привет.\n" +
            "Чтобы создать уведомление отправьте мне сообщение согласно паттерну:\n" +
            "дд.мм.гггг чч:мм <Текст уведомления> ";

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private MessageService messageService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (Objects.isNull(update.message())) {
                logger.info("Отправлено исправленное сообщение");
                sendMessage(update.editedMessage().chat().id(), "Извините, я не работаю с исправленными сообщениями");
                return;
            }
            Matcher matcher = pattern.matcher(update.message().text());
            if (update.message().text().equals("/start")) {
                logger.info("Отправлен ответ на команду \"/start\"");
                sendMessage(update.message().chat().id(), startMessage);
            } else if (matcher.matches()) {
                LocalDateTime date = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                String item = matcher.group(3);
                try {
                    if (!date.isAfter(LocalDateTime.now())) {
                        throw new RuntimeException("Неправильная дата");
                    }
                    Message message = new Message(null, update.message().chat().id(), item, date);
                    sendMessage(update.message().chat().id(), "Уведомление успешно создано, ожидайте");
                    logger.info("Уведомление создано");
                    messageService.create(message);
                } catch (RuntimeException e) {
                    logger.info("Отправлена неправильная дата");
                    sendMessage(update.message().chat().id(), "Неправильная дата");
                }
            } else {
                logger.info("Отправлена неправильная команда для бота");
                sendMessage(update.message().chat().id(), "Неправильная команда для бота");
            }
            // Process your updates here
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void Scheduled() {
        List<Message> messages = messageService.findMessage(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        messages.forEach(task -> {
            logger.info("Отправлено уведомление: chatId = " + task.getChatId() + " , text = " + task.getText());
            sendMessage(task.getChatId(), task.getText());
            logger.info("Удалено уведомление из БД");
            messageService.delete(task.getId());
        });
        List<Message> messages1 = messageService.findIsAfterMessage();
        messages1.forEach(task -> {
            logger.info("Отправлено прошедшее уведомление: chatId = " + task.getChatId() + " , text = " + task.getText());
            sendMessage(task.getChatId(), task.getDate() + " " + task.getText());
            logger.info("Удалено уведомление из БД");
            messageService.delete(task.getId());
        });

    }

    private SendResponse sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        return telegramBot.execute(message);
    }

}
