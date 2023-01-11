package cz.osu.chatappbe.services.utility;

import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.SignupForm;
import cz.osu.chatappbe.services.models.ChatRoomService;
import cz.osu.chatappbe.services.models.MessageService;
import cz.osu.chatappbe.services.models.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {
	@Autowired
	private final UserService userService;
	@Autowired
	private final ChatRoomService chatRoomService;
	@Autowired
	private final MessageService messageService;
	@Autowired
	private final RabbitMQService rabbitMQService;
	
	public ResponseEntity<String> register(SignupForm signupForm) {
		if (userService.exists(signupForm.getUsername())) {
			return new ResponseEntity<>("Username already exists!", HttpStatus.BAD_REQUEST);
		} else {
			try {
				ChatUser user = userService.create(signupForm);
				
				rabbitMQService.createQueue("public-queue-" + user.getUsername());
				
				for (ChatUser chatUser : userService.list()) {
					if (chatUser.getId().equals(user.getId())) {
						continue;
					}
					
					ChatRoom chatRoom = chatRoomService.create(user, chatUser);
					
					userService.addRoom(user, chatRoom);
					userService.addRoom(chatUser, chatRoom);
					
					messageService.create(user, chatRoom, "Hello, " + chatUser.getUsername() + "!");
				}
				
				return new ResponseEntity<>("Successfully registered!", HttpStatus.OK);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
			}
		}
	}
}
