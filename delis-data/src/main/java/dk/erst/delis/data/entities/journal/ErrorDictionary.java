package dk.erst.delis.data.entities.journal;

import dk.erst.delis.data.annotations.WebApiContent;
import dk.erst.delis.data.entities.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@WebApiContent
public class ErrorDictionary extends AbstractEntity {

	@Column(nullable = false)
	private String code;
	
	@Column(nullable = true)
	private String message;

	@Column
	private String flag;

	@OneToMany(
			mappedBy = "errorDictionary",
			cascade = CascadeType.ALL,
			orphanRemoval = true
	)
	List<JournalDocumentError> documents = new ArrayList<>();
}
