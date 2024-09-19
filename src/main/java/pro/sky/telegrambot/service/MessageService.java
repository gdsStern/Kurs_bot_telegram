package pro.sky.telegrambot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.sky.telegrambot.model.Message;
import pro.sky.telegrambot.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Transactional
    public Message create(Message message) {
        return messageRepository.save(message);
    }
    @Transactional
    public List<Message> findMessage(LocalDateTime date) {
        return new ArrayList<>(messageRepository.findAllByDate(date));
    }
    @Transactional
    public List<Message> findIsAfterMessage() {
        return new ArrayList<>(messageRepository.findIsAfterDate());
    }
    @Transactional
    public void delete(Long id) {
        messageRepository.deleteById(id);
    }
}
