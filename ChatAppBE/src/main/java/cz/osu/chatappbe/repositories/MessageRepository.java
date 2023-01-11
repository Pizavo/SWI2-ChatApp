package cz.osu.chatappbe.repositories;

import cz.osu.chatappbe.models.DB.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
}
