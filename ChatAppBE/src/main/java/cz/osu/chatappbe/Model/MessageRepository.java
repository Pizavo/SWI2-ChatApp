package cz.osu.chatappbe.Model;

import cz.osu.chatappbe.Model.DB.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepository extends CrudRepository<Message, Integer> {
}
