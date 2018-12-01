package dk.erst.delis.data;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class IdentifierGroup {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(cascade = CascadeType.REFRESH)
	@JoinColumn(name = "ORGANISATION_ID", nullable = false)
	private Organisation organisation;
	
	@Column(nullable = false, length=20)
	private String code;

	@Column(nullable = false, length=20)
	private String name;
	
}
