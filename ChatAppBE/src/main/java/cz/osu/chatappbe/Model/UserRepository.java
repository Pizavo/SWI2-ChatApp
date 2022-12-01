package cz.osu.chatappbe.Model;

import cz.osu.chatappbe.Model.DB.ChatUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<ChatUser, Integer> {

    List<ChatUser> findAll();

    ChatUser findUserByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
