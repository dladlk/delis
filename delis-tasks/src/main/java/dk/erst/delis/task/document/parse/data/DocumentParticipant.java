package dk.erst.delis.task.document.parse.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentParticipant {

	@XmlElement(name = "ID")
	private String id;

	@XmlElement(name = "SchemeID")
	private String schemeId;

	@XmlElement(name = "Name")
	private String name;

	@XmlElement(name = "Country")
	private String country;

	public String encodeID() {
		return schemeId + "::" + id;
	}

}
