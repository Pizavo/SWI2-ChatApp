package cz.osu.chatappbe.controllers;

import cz.osu.chatappbe.services.models.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/api/chatroom")
public class ChatRoomController {
	@Autowired
	private ChatRoomService chatRoomService;
	
	@GetMapping("/list")
	public ResponseEntity<Object> getChatRooms(@RequestBody String username) {
		return new ResponseEntity<>(this.chatRoomService.getUserRooms(username), HttpStatus.OK);
	}
}
