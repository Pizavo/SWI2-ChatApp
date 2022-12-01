package cz.osu.chatappbe.Model;

import cz.osu.chatappbe.Model.DB.ChatRoom;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ChatRoomRepository extends CrudRepository<ChatRoom, Integer> {

    List<ChatRoom> findByJoinedUsers_Username(String username);

    ChatRoom findChatRoomByIsPublicIsTrue();

}
