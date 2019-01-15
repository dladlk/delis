package dk.erst.delis.data.entities.access;

import dk.erst.delis.data.entities.AbstractEntity;
import dk.erst.delis.data.enums.access.AccessPointType;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

import java.sql.Blob;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AccessPoint extends AbstractEntity {

	@Column(nullable = false)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccessPointType type;

	@Column
	private String certificateCN;

	@Column
	private Blob certificate;
}
