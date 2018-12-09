package dk.erst.delis.data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(indexes = {
		
		@Index(name="I_ORGANISAION_ID", columnList="ORGANISATION_ID, STATUS, PUBLISHING_STATUS"),
		
		@Index(name="I_ORGANISAION_ID", columnList="LAST_SYNC_ORGANISATION_FACT_ID"),

})
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
	
	@Column(name="PUBLISHING_STATUS", nullable = true)
	private IdentifierPublishingStatus publishingStatus;
	
	@Column(nullable = false, length=128)
	private String name;
	
	@Column(name="LAST_SYNC_ORGANISATION_FACT_ID", nullable = false)
	private long lastSyncOrganisationFactId;
}
