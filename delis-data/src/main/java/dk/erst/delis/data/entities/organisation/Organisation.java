package dk.erst.delis.data.entities.organisation;

import javax.persistence.Column;
import javax.persistence.Entity;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Organisation extends AbstractCreateUpdateEntity {
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String code;
}
