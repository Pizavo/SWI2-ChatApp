package cz.osu.chatappbe.repositories;

import cz.osu.chatappbe.models.DB.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {
}
