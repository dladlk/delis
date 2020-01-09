package dk.erst.delis.data.entities.access;

import java.sql.Blob;
import java.util.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;
import dk.erst.delis.data.enums.access.AccessPointType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class AccessPoint extends AbstractCreateUpdateEntity {

	@Column(nullable = false)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccessPointType type;

	@Column
	private String certificateCN;

	@Column
	private String serviceDescription;

	@Column
	private String technicalContactUrl;

	@Column
	private Blob certificate;
	
	public byte[] decodeCertificateToBytes() throws Exception {
		if (certificate == null) {
			return null;
		}
		return Base64.getDecoder().decode(certificate.getBytes(1L, (int) certificate.length()));
	}
}
