package cz.osu.chatappbe.Service;

import cz.osu.chatappbe.Model.DB.ChatUser;
import cz.osu.chatappbe.Model.SignupForm;
import cz.osu.chatappbe.Model.UserRepository;
import cz.osu.chatappbe.Model.Json.UserToken;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    public ResponseEntity<Object> authenticate(SignupForm loginForm) {
        ChatUser user = userRepository.findUserByUsernameIgnoreCase(loginForm.getUsername());
        if (user != null) {
            if (user.getPassword().equals(loginForm.getPassword())) {
                UserToken userToken = new UserToken(user.getUserId(), user.getUsername());
                return new ResponseEntity<>(userToken, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Password is incorrect!", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Username doesn't exist!", HttpStatus.BAD_REQUEST);
        }
    }
}
