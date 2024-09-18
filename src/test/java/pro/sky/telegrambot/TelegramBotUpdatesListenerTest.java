package pro.sky.telegrambot;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import pro.sky.telegrambot.listener.TelegramBotUpdatesListener;
import pro.sky.telegrambot.model.Message;
import pro.sky.telegrambot.repository.MessageRepository;
import pro.sky.telegrambot.service.MessageService;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@WebMvcTest(TelegramBotUpdatesListener.class)
class TelegramBotUpdatesListenerTest {
    @MockBean
    MessageRepository messageRepository;

    @SpyBean
    MessageService messageService;
    @MockBean
    TelegramBot telegramBot;
    @SpyBean
    TelegramBotUpdatesListener telegramBotUpdatesListener;

    @Captor
    private ArgumentCaptor<SendMessage> massageCaptor;
    @Captor
    private ArgumentCaptor<Message> taskArgumentCaptor;

    @Test
    void processTest1() {
        //data
        Update update = mock(Update.class);
        com.pengrad.telegrambot.model.Message message = mock(com.pengrad.telegrambot.model.Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("/start");
        when(message.chat()).thenReturn(mock(com.pengrad.telegrambot.model.Chat.class));
        when(message.chat().id()).thenReturn(557789L);
        List<Update> updates = new ArrayList<>();
        updates.add(update);
        String expectedText = "Привет.\n" +
                "Чтобы создать уведомление отправьте мне сообщение согласно паттерну:\n" +
                "дд.мм.гггг чч:мм <Текст уведомления> ";
        //test
        telegramBotUpdatesListener.process(updates);
        verify(telegramBot).execute(massageCaptor.capture());
        SendMessage capturedMessage = massageCaptor.getValue();
        //check
        assertEquals(557789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals(expectedText, capturedMessage.getParameters().get("text"));
    }

    @Test
    void processTest2() {
        Update update = mock(Update.class);
        com.pengrad.telegrambot.model.Message message = mock(com.pengrad.telegrambot.model.Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("18.09.2024 20:45 hi");
        when(message.chat()).thenReturn(mock(com.pengrad.telegrambot.model.Chat.class));
        when(message.chat().id()).thenReturn(557789L);
        List<Update> updates = new ArrayList<>();
        updates.add(update);
        String expectedText = "Неправильная дата";

        telegramBotUpdatesListener.process(updates);
        verify(telegramBot).execute(massageCaptor.capture());
        SendMessage capturedMessage = massageCaptor.getValue();

        assertEquals(557789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals(expectedText, capturedMessage.getParameters().get("text"));
    }

    @Test
    void processTest3() {
        Update update = mock(Update.class);
        com.pengrad.telegrambot.model.Message message = mock(com.pengrad.telegrambot.model.Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("18.09.2029 20:45 hi");
        when(message.chat()).thenReturn(mock(com.pengrad.telegrambot.model.Chat.class));
        when(message.chat().id()).thenReturn(557789L);
        List<Update> updates = new ArrayList<>();
        updates.add(update);
        String expectedText = "Уведомление успешно создано, ожидайте";

        telegramBotUpdatesListener.process(updates);
        verify(telegramBot).execute(massageCaptor.capture());
        SendMessage capturedMessage = massageCaptor.getValue();

        assertEquals(557789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals(expectedText, capturedMessage.getParameters().get("text"));
    }

    @Test
    void processTest4() {
        Update update = mock(Update.class);
        com.pengrad.telegrambot.model.Message message = mock(com.pengrad.telegrambot.model.Message.class);
        when(update.message()).thenReturn(message);
        when(message.text()).thenReturn("hi");
        when(message.chat()).thenReturn(mock(com.pengrad.telegrambot.model.Chat.class));
        when(message.chat().id()).thenReturn(557789L);
        List<Update> updates = new ArrayList<>();
        updates.add(update);
        String expectedText = "Неправильная команда для бота";

        telegramBotUpdatesListener.process(updates);
        verify(telegramBot).execute(massageCaptor.capture());
        SendMessage capturedMessage = massageCaptor.getValue();

        assertEquals(557789L, capturedMessage.getParameters().get("chat_id"));
        assertEquals(expectedText, capturedMessage.getParameters().get("text"));
    }
}