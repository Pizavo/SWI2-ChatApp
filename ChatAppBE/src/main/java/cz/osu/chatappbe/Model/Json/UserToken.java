package cz.osu.chatappbe.Model.Json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserToken {
    private int userId;
    private String username;
}
