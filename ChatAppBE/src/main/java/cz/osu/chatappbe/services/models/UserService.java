package cz.osu.chatappbe.services.models;

import cz.osu.chatappbe.models.DB.ChatRoom;
import cz.osu.chatappbe.models.DB.ChatUser;
import cz.osu.chatappbe.models.SignupForm;
import cz.osu.chatappbe.repositories.UserRepository;
import cz.osu.chatappbe.services.utility.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
	@Autowired
	private UserRepository repository;
	@Autowired
	private ChatRoomService chatRoomService;
	@Autowired
	private HashService hashService;
	
	public ChatUser create(SignupForm signupForm) {
		ChatUser user = new ChatUser();
		
		ChatRoom room = chatRoomService.getPublicRoom().orElseGet(() -> chatRoomService.create());
		
		user.setUsername(signupForm.getUsername());
		user.setPassword(this.hashService.hash(signupForm.getPassword()));
		user.addRoom(room);
		
		return this.repository.save(user);
	}
	
	public void addRoom(ChatUser user, ChatRoom room) {
		user.addRoom(room);
		
		this.repository.save(user);
	}
	
	public boolean exists(String username) {
		return this.repository.existsByUsernameIgnoreCase(username);
	}
	
	public List<ChatUser> list() {
		List<ChatUser> users = this.repository.findAll();
		
		/*users.forEach(chatUser -> chatUser.setJoinedRooms(null));
		users.forEach(chatUser -> chatUser.setMessages(null));*/
		
		return users;
	}
	
	public Optional<ChatUser> login(SignupForm loginForm) {
		Optional<ChatUser> user = this.get(loginForm.getUsername());
		
		if (user.isEmpty() || !this.hashService.verify(user.get().getPassword(), loginForm.getPassword())) {
			return Optional.empty();
		}
		
		return user;
	}
	
	public Optional<ChatUser> get(String username) {
		return repository.findUserByUsernameIgnoreCase(username);
	}
	
	public Optional<ChatUser> get(Integer id) {
		return repository.findById(id);
	}
}
