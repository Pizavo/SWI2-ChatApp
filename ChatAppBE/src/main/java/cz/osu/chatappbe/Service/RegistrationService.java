package cz.osu.chatappbe.Service;

import cz.osu.chatappbe.Config.RabbitMQConfig;
import cz.osu.chatappbe.Model.ChatRoomRepository;
import cz.osu.chatappbe.Model.DB.ChatRoom;
import cz.osu.chatappbe.Model.DB.ChatUser;
import cz.osu.chatappbe.Model.DB.Message;
import cz.osu.chatappbe.Model.MessageRepository;
import cz.osu.chatappbe.Model.SignupForm;
import cz.osu.chatappbe.Model.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class RegistrationService {
    private UserRepository userRepository;
    private ChatRoomRepository chatRoomRepository;
    private MessageRepository messageRepository;
    @Autowired
    private AmqpAdmin admin;

    public ResponseEntity<String> register(SignupForm signupForm) {
        if (userRepository.existsByUsernameIgnoreCase(signupForm.getUsername())) {
            return new ResponseEntity<>("Username already exists!", HttpStatus.BAD_REQUEST);
        } else {
            try {
                ChatUser user = new ChatUser();
                user.setUsername(signupForm.getUsername());
                // password encryption
                user.setPassword(signupForm.getPassword());
                user.addRoom(chatRoomRepository.findChatRoomByIsPublicIsTrue());
                Integer userId = userRepository.save(user).getUserId();

                // Rabbit public queue
                String queueName = "public-queue-" + user.getUsername();
                Queue queue = new Queue(queueName, false);
                Binding binding = new Binding(queueName, Binding.DestinationType.QUEUE, RabbitMQConfig.exchange, "", null);
                admin.declareQueue(queue);
                admin.declareBinding(binding);

                // Pro chat 1:1 mezi vsemi
                for (ChatUser chatUser : userRepository.findAll()) {
                    if (chatUser.getUserId().equals(userId)) {
                        continue;   // pokud jde o stejného usera, pokračuju dál v cyklu
                    }
                    ChatRoom chatRoom = new ChatRoom();
                    chatRoom.setChatName(user.getUsername() + ", " + chatUser.getUsername());
                    chatRoom.setIsPublic(false);
                    user.addRoom(chatRoom);
                    chatUser.addRoom(chatRoom);
                    chatRoomRepository.save(chatRoom);

                    // Prvotni zpravy (testovaci zprava)
                    Message message = new Message();
                    message.setContent("Bruce Wayne je Batman!");
                    message.setSendTime(new Date());
                    user.addMessage(message);
                    chatRoom.addMessage(message);
                    messageRepository.save(message);
                }

                return new ResponseEntity<>("Successfully registered!", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(e.getMessage(), HttpStatus.EXPECTATION_FAILED);
            }
        }
    }
}
