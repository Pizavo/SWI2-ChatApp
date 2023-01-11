package cz.osu.chatappbe.models.Json;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class UserToken {
	private UUID userId;
	private String username;
}
