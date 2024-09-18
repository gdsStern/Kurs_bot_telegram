package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.Message;
import pro.sky.telegrambot.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageService {
    @Autowired
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message create(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findMessage(LocalDateTime date) {
        return new ArrayList<>(messageRepository.findAllByDate(date));
    }

    public List<Message> findIsAfterMessage() {
        return new ArrayList<>(messageRepository.findIsAfterDate());
    }

    public void delete(Long id) {
        messageRepository.deleteById(id);
    }
}
