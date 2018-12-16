package dk.erst.delis.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data
public class RuleDocumentValidation {

	@Id
	@Column(name = "ID_PK")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private boolean active;
	
	@Column(nullable = false)
	private int priority;
	
	@Column(nullable = false)
	private DocumentFormat documentFormat;
	
	@Column(nullable = false)
	private RuleDocumentValidationType validationType;
	
	@Column(nullable = false)
	private String rootPath;
	
	@Column(nullable = true)
	private String config;
}
