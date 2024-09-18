package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue
    private Long id;
    private Long chatId;
    private String text;
    private LocalDateTime date;

    public Message(Long id, Long chatId, String text, LocalDateTime date) {
        this.id = id;
        this.chatId = chatId;
        this.text = text;
        this.date = date;
    }

    public Message() {

    }

    public Long getId() {
        return id;
    }

    public Long getChatId() {
        return chatId;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

//Предположу, что нам потребуется иметь первичный ключ в таблице, идентификатор чата,
//в который нужно отправить уведомление, текст уведомления и дату+время,
//когда требуется отправить уведомление. Возможно, вы захотите хранить какие-то дополнительные данные.

//Расскажите о принципе наследования в ООП. Зачем он нужен?
//Что такое агрегатные операторы? (тема «Базы данных»)
//Какая реализация интерфейса Map позволяет сохранить порядок добавления элементов?
