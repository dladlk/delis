package dk.erst.delis.data;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class JournalIdentifier {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "IDENTIFIER_ID", nullable = false)
	private Identifier identifier;
	
	@Column(name = "CREATE_TIME", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createTime;
	
	@Column(nullable = false)
	private String message;
	
	@Column(nullable = true)
	private Long durationMs;
}
