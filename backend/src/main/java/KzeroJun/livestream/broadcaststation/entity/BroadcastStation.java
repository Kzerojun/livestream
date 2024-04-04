package KzeroJun.livestream.broadcaststation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BroadcastStation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "broadcast_station_id")
	private Long id;

	private String description;
	
	private String image;



}
