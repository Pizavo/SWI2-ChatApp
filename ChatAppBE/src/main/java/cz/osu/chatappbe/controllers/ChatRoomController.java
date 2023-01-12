package cz.osu.chatappbe.controllers;

import cz.osu.chatappbe.models.ChatForm;
import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.services.models.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/chatroom")
public class ChatRoomController {
	@Autowired
	private ChatRoomService chatRoomService;
	
	@PostMapping("/create")
	public ResponseEntity<ChatRoom> createChatRoom(@RequestBody ChatForm chatForm) {
		return new ResponseEntity<>(this.chatRoomService.prepareRoomForFrontEnd(this.chatRoomService.create(chatForm)), HttpStatus.CREATED);
	}
	
	@GetMapping("/list")
	public ResponseEntity<Object> getChatRooms(@RequestBody String username) {
		return new ResponseEntity<>(this.chatRoomService.getUserRooms(username), HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Object> getChatRoom(@PathVariable Integer id) {
		Optional<ChatRoom> chatRoom = this.chatRoomService.getChatRoomForFrontEnd(id);
		
		return chatRoom.<ResponseEntity<Object>>map(room -> new ResponseEntity<>(room, HttpStatus.OK))
		               .orElseGet(() -> new ResponseEntity<>("Chatroom " + id + " does not exist.", HttpStatus.BAD_REQUEST));
	}
	
	@GetMapping("/public")
	public ResponseEntity<Object> getPublicChatRoom() {
		ChatRoom room = this.chatRoomService.getPublicRoomForFrontEnd().get();
		room.setJoinedUsers(new ArrayList<>());
		room.getMessages().forEach(message -> {
			message.getRoom().setMessages(new ArrayList<>());
			message.getUser().setMessages(new ArrayList<>());
			message.getUser().setJoinedRooms(new ArrayList<>());
		});
		
		return new ResponseEntity<>(room, HttpStatus.OK);
	}
}
