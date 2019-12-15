package dk.erst.delis.data.entities.user;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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

	/*
	 * Defines DISABLED status of user
	 */
	@Column
	private boolean disabled;

	/*
	 * Defines CREDENTIALS_EXPIRED status of User
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "password_change_time", nullable = true)
	private Date passwordChangeTime;

	/*
	 * Only for information
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login_time", nullable = true)
	private Date lastLoginTime;

	/*
	 * Together with lastInvalidLoginTime defines LOCKED status of User
	 * 
	 * - if number of invalid counts more than limit
	 * 
	 * and last attempt was done more than given amount of minutes ago
	 */
	@Column(name = "invalid_login_count", nullable = true)
	private Integer invalidLoginCount;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_invalid_login_time", nullable = true)
	private Date lastInvalidLoginTime;

}
