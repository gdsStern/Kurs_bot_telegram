package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

    private final Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
    private final String startMessage = "Привет.\n" +
            "Чтобы создать уведомление отправьте мне сообщение согласно паттерну:\n" +
            "дд.мм.гггг чч:мм <Текст уведомления> ";
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final TelegramBot telegramBot;

    private final MessageService messageService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, MessageService messageService) {
        this.telegramBot = telegramBot;
        this.messageService = messageService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            if (Objects.isNull(update.message())) {
                sendMessage(update.editedMessage().chat().id(), "Извините, я не работаю с исправленными сообщениями");
                logger.info("Отправлено исправленное сообщение");
                return;
            }
            try {
                if (Objects.isNull(update.message().text())) {
                    throw new RuntimeException("Нет текста");
                }
            } catch (RuntimeException e) {
                sendMessage(update.message().chat().id(), "Я работаю только с текстом");
                logger.info("Отправлен не текст");
                return;
            }

            Matcher matcher = pattern.matcher(update.message().text());
            if (update.message().text().equals("/start")) {
                sendMessage(update.message().chat().id(), startMessage);
                logger.info("Отправлен ответ на команду \"/start\"");
            } else if (matcher.matches()) {
                LocalDateTime date = LocalDateTime.parse(matcher.group(1), dateTimeFormatter);
                String item = matcher.group(3);
                try {
                    if (!date.isAfter(LocalDateTime.now())) {
                        throw new RuntimeException("Неправильная дата");
                    }
                    Message message = new Message(update.message().chat().id(), item, date);
                    sendMessage(update.message().chat().id(), "Уведомление успешно создано, ожидайте");
                    messageService.create(message);
                    logger.info("Уведомление создано");
                } catch (RuntimeException e) {
                    sendMessage(update.message().chat().id(), "Неправильная дата");
                    logger.info("Отправлена неправильная дата");
                }
            } else {
                sendMessage(update.message().chat().id(), "Неправильная команда для бота");
                logger.info("Отправлена неправильная команда для бота");
            }

            // Process your updates here
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


    private SendResponse sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage(chatId, text);
        return telegramBot.execute(message);
    }

}
