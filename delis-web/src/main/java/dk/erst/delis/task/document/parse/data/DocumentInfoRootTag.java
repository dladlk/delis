package dk.erst.delis.task.document.parse.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class DocumentInfoRootTag {

	@XmlElement(name="RootTag")
	private String rootTag;
	
	@XmlElement(name="NameSpace")
	private String namespace;
}
