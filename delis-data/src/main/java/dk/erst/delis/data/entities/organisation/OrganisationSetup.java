package dk.erst.delis.data.entities.organisation;

import javax.persistence.*;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.enums.organisation.OrganisationSetupKey;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(indexes = {

		@Index(name = "I_ORGANISAION_ID", columnList = "ORGANISATION_ID, SETUP_KEY", unique = true),

})
public class OrganisationSetup extends AbstractCreateUpdateEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;

	@Column(name = "SETUP_KEY", nullable = false)
	@Convert(converter = dk.erst.delis.data.enums.organisation.OrganisationSetupKeyConverter.class)
	private OrganisationSetupKey key;

	@Column(name = "SETUP_VALUE", nullable = false, length = 250)
	private String value;
}
