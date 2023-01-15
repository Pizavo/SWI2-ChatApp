package cz.osu.chatappbe.repositories;

import cz.osu.chatappbe.models.DB.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<ChatUser, Integer> {
	Optional<ChatUser> findUserByUsernameIgnoreCase(String username);
	
	boolean existsByUsernameIgnoreCase(String username);
}
