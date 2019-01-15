package dk.erst.delis.data.entities.identifier;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.entities.organisation.Organisation;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@Entity
public class IdentifierGroup extends AbstractEntity {

	public static final String DEFAULT_CODE = "default";

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;
	
	@Column(nullable = false, length=20)
	private String code;

	@Column(nullable = false, length=20)
	private String name;
	
}
