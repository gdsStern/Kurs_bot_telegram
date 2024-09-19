package pro.sky.telegrambot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pro.sky.telegrambot.model.Message;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByDate(LocalDateTime date);
    @Query(value = "SELECT * FROM message where date < current_timestamp", nativeQuery = true)
    List<Message> findIsAfterDate();

}
