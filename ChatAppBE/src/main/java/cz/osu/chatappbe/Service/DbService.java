package cz.osu.chatappbe.Service;

import cz.osu.chatappbe.Model.ChatRoomRepository;
import cz.osu.chatappbe.Model.DB.ChatRoom;
import cz.osu.chatappbe.Model.DB.ChatUser;
import cz.osu.chatappbe.Model.DB.Message;
import cz.osu.chatappbe.Model.MessageRepository;
import cz.osu.chatappbe.Model.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DbService {

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    @Getter
    private final MessageRepository messageRepository;

    public ResponseEntity<List<ChatUser>> getUsers() {
        List<ChatUser> retList = userRepository.findAll();
        retList.forEach(chatUser -> chatUser.setJoinedRooms(null));
        retList.forEach(chatUser -> chatUser.setMessages(null));
        return new ResponseEntity<>(retList, HttpStatus.OK);
    }

    public ResponseEntity<List<ChatRoom>> getChatRooms(String username) {
        List<ChatRoom> retList = chatRoomRepository.findByJoinedUsers_Username(username);
        retList.forEach(chatRoom -> chatRoom.setJoinedUsers(null));
        for (ChatRoom chatRoom : retList) {
            for (Message message : chatRoom.getMessages()) {
                message.setChatRoom(null);
                message.getChatUser().setMessages(null);
                message.getChatUser().setJoinedRooms(null);
            }
        }
        return new ResponseEntity<>(retList, HttpStatus.OK);
    }

    public ChatUser getUser(String username) {
        return userRepository.findUserByUsernameIgnoreCase(username);
    }

    public ChatRoom getPublicChatroom() {
        return chatRoomRepository.findChatRoomByIsPublicIsTrue();
    }

}
