package cz.osu.chatappbe.models.DB;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "chat_room_id", nullable = false)
	private ChatRoom room;
	
	@ManyToOne
	@JoinColumn(name = "chat_user_id", nullable = false)
	private ChatUser user;
	
	@Column(nullable = false)
	private String content;
	
	@Column(nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendTime;
	
	public String toString() {
		return "Message{" +
		       "id=" + id +
		       ", room=" + room.getId() +
		       ", user=" + user.getId() +
		       ", content='" + content + '\'' +
		       ", sendTime=" + sendTime +
		       '}';
	}
}
