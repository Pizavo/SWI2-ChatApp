package cz.osu.chatappbe.services.utility;

import cz.osu.chatappbe.config.RabbitMQConfig;
import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.DB.Message;
import cz.osu.chatappbe.models.PayloadMsg;
import cz.osu.chatappbe.rabbit.Sender;
import cz.osu.chatappbe.services.models.ChatRoomService;
import cz.osu.chatappbe.services.models.MessageService;
import cz.osu.chatappbe.services.models.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class MessagingService {
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	@Autowired
	private MessageService messageService;
	@Autowired
	private UserService userService;
	@Autowired
	private ChatRoomService chatRoomService;
	@Autowired
	private Sender sender;
	
	public PayloadMsg receivePublicMessage(PayloadMsg msg) {
		Optional<ChatUser> user = userService.get(msg.getSenderName());
		Optional<ChatRoom> room = chatRoomService.getPublicRoom();
		
		if (user.isEmpty() || room.isEmpty()) {
			return null;
		}
		
		//ChatUser user = user.get();
		
		Message publicMessage = messageService.create(user.get(), room.get(), msg.getContent(), msg.getDate());
		
		/*ChatRoom receivingRoom = new ChatRoom(dbService.getPublicChatroom());
		receivingRoom.setMessages(null);
		receivingRoom.setJoinedUsers(null);
		publicMessage.setChatRoom(receivingRoom);
		
		ChatUser senderUser = new ChatUser(dbService.getUser(msg.getSenderName()).get());
		senderUser.setMessages(null);
		senderUser.setJoinedRooms(null);
		publicMessage.setChatUser(senderUser);*/
		
		sender.sendPublic(RabbitMQConfig.exchange, String.valueOf(publicMessage));
		
		return msg;
	}
	
	public PayloadMsg receiveGroupMessage(@Payload PayloadMsg msg) {
		// todo: poslat message do queue od všech uživatelů kteří jsou u dané ChatRoomId (GroupId)
		// todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
		// todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny
		
		Optional<ChatUser> user = userService.get(msg.getSenderName());
		Optional<ChatRoom> room = chatRoomService.getGroupRoom(UUID.fromString(msg.getReceiverGroupId()));
		
		if (user.isEmpty() || room.isEmpty()) {
			return null;
		}
		
		Message publicMessage = messageService.create(user.get(), room.get(), msg.getContent(), msg.getDate());
		
		String destination = "/chatroom/" + msg.getReceiverGroupId();
		simpMessagingTemplate.convertAndSend(destination, msg);
		return msg;
	}
	
	public PayloadMsg receivePrivateMessage(@Payload PayloadMsg msg) {
		// todo: poslat do queue od sendera a receivera
		// todo: asi ulozit zpravu do databaze (vzdy ji uklada ten, co ji odesila)
		// todo: poslat web socket zpravu aby si uzivatel vyzvedl zpravu z queue pokud je prihlaseny
		
		// url: /user/username/private
		simpMessagingTemplate.convertAndSendToUser(msg.getReceiverName(), "/private", msg);
		return msg;
	}
}
