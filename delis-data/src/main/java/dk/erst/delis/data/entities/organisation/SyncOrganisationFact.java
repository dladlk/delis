package dk.erst.delis.data.entities.organisation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import dk.erst.delis.data.entities.AbstractCreateUpdateEntity;

import lombok.Getter;
import lombok.Setter;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(indexes =  {@Index(name = "SOF_ORGANISATION_ID", columnList = "ORGANISATION_ID")})
public class SyncOrganisationFact extends AbstractCreateUpdateEntity {

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;
	
	@Column(nullable = false)
	private String description;
	
	@Column(nullable = true)
	private Long durationMs;
	
	@Column(nullable = false)
	private int total;
	
	@Column(name="CNT_ADD", nullable = false)
	private int add;
	
	@Column(name="CNT_UPDATE", nullable = false)
	private int update;
	
	@Column(name="CNT_DELETE", nullable = false)
	private int delete;
	
	@Column(name="CNT_EQUAL", nullable = false)
	private int equal;
	
	@Column(name="CNT_FAILED", nullable = false)
	private int failed;
	
	/*
	 * Increment methods
	 */
	
	public int incrementTotal() {
		return this.total++;
	}

	public int incrementAdd() {
		return this.add++;
	}

	public int incrementUpdate() {
		return this.update++;
	}

	public int incrementDelete() {
		return this.delete++;
	}

	public int incrementEqual() {
		return this.equal++;
	}

	public int incrementFailed() {
		return this.failed++;
	}
}
