package dk.erst.delis.data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Identifier {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "IDENTIFIER_GROUP_ID", nullable = false)
	private IdentifierGroup identifierGroup;
	
	@Column(nullable = false, length=20)
	private String value;
	
	@Column(nullable = false, length=20)
	private String type;
	
	@Column(nullable = false)
	private IdentifierStatus status;
	
	@Column(nullable = true)
	private IdentifierPublishingStatus publishingStatus;
	
	@Column(nullable = false, length=128)
	private String name;
}
