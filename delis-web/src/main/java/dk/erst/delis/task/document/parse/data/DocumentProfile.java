package dk.erst.delis.task.document.parse.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentProfile {

	@XmlElement(name="ID")
	private String id;
	
	@XmlElement(name="SchemeID")
	private String schemeID;

	@XmlElement(name="SchemeAgencyID")
	private String schemeAgencyId;

}
