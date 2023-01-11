package cz.osu.chatappbe.controllers;

import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.services.models.ChatRoomService;
import cz.osu.chatappbe.services.models.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class DbController {
	@Autowired
	private ChatRoomService chatRoomService;
	@Autowired
	private UserService userService;
	
	@GetMapping("/users")
	public ResponseEntity<List<ChatUser>> getUsers() {
		return new ResponseEntity<>(userService.list(), HttpStatus.OK);
	}
	
	@GetMapping("/chatrooms")
	public ResponseEntity<List<ChatRoom>> getChatRooms(@RequestParam String username) {
		System.out.println(username);
		List<ChatRoom> chatRooms = chatRoomService.getUserRooms(username);
		System.out.println(chatRooms);
		
		return new ResponseEntity<>(chatRooms, HttpStatus.OK);
	}
	
}
