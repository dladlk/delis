package dk.erst.delis.data;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(indexes =  {@Index(name = "SOF_ORGANISATION_ID", columnList = "ORGANISATION_ID")})
public class SyncOrganisationFact {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;

	@Column(name = "CREATE_TIME", nullable = false, updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreatedDate
	private Date createTime;
	
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
