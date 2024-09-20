package pro.sky.telegrambot.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue
    private Long id;
    //@Column(name = "id")
    private Long chatId;
   // @Column(name = "message")
    private String text;
   // @Column(name = "date")
    private LocalDateTime date;

    public Message(Long chatId, String text, LocalDateTime date) {
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

    public String getTextMessage() {
        return text;
    }

    public LocalDateTime getData() {
        return date;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
