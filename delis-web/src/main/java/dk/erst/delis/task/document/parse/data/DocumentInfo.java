package dk.erst.delis.task.document.parse.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@XmlRootElement(name = "Info")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class DocumentInfo {

	@XmlElement(name = "DocumentRoot")
	private DocumentInfoRootTag root;

	@XmlElement(name = "ID")
	private String id;

	@XmlElement(name = "Date")
	private String date;

	@XmlElement(name = "CustomizationID")
	private String customizationID;

	@XmlElement(name = "Profile")
	private DocumentProfile profile;

	@XmlElement(name = "Sender")
	private DocumentParticipant sender;

	@XmlElement(name = "Receiver")
	private DocumentParticipant receiver;
	
	@XmlElement(name = "AmountNegative")
	private boolean amountNegative;

}
