package dk.erst.delis.data.entities.identifier;

import javax.persistence.*;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierPublishingStatus;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@WebApiContent
@ToString
@Table(indexes = {

		@Index(name="I_ORGANISAION_ID", columnList="ORGANISATION_ID, STATUS, PUBLISHING_STATUS"),
		
		@Index(name="I_ORGANISAION_ID", columnList="LAST_SYNC_ORGANISATION_FACT_ID"),

		@Index(name="UK_UNIQUE_VALUE_TYPE", columnList="UNIQUE_VALUE_TYPE", unique = true),

})
public class Identifier extends AbstractCreateUpdateEntity {

	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;
	
	@ManyToOne(cascade = CascadeType.DETACH)
	@JoinColumn(name = "IDENTIFIER_GROUP_ID", nullable = false)
	private IdentifierGroup identifierGroup;
	
	@Column(nullable = false, length=20)
	private String value;
	
	@Column(nullable = false, length=20)
	private String type;
	
	@Column(name="UNIQUE_VALUE_TYPE", nullable = false, length = 40)
	private String uniqueValueType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private IdentifierStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name="PUBLISHING_STATUS", nullable = true, length = 10)
	private IdentifierPublishingStatus publishingStatus;
	
	@Column(nullable = false, length=128)
	private String name;
	
	@Column(name="LAST_SYNC_ORGANISATION_FACT_ID", nullable = false)
	private long lastSyncOrganisationFactId;
}
