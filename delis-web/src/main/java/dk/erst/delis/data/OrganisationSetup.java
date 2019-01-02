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

		@Index(name = "I_ORGANISAION_ID", columnList = "ORGANISATION_ID, KEY", unique = true),

})
public class OrganisationSetup {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;

	@Column(name = "KEY", nullable = false, length = 50)
	private OrganisationSetupKey key;

	@Column(name = "VALUE", nullable = false, length = 250)
	private String value;

}
