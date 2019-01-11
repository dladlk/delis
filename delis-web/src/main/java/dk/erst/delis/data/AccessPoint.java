package dk.erst.delis.data;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class AccessPoint {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccessPointType type;

	@Column
	private String certificateCN;

	@Column
	private Blob certificate;
}
