package dk.erst.delis.data.entities.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.listeners.FullNameGenerationListener;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user", indexes = { @Index(name = "USR_ORGANISATION_ID", columnList = "ORGANISATION_ID") })
@EntityListeners({ FullNameGenerationListener.class })
public class User extends AbstractCreateUpdateEntity {

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	private String firstName;
	private String lastName;

	@Email
	private String email;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = true)
	private Organisation organisation;

	@Transient
	private String fullName;
	
	@Column(nullable = true)
	private Boolean disabledIrForm;

	@Column
	private boolean disabled;
}
