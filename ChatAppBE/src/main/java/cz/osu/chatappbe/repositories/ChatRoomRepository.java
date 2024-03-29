package cz.osu.chatappbe.repositories;

import cz.osu.chatappbe.models.DB.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
	
	List<ChatRoom> findByJoinedUsers_Username(String username);
	
	Optional<ChatRoom> findChatRoomByIsPublicIsTrue();
	
}
