package dk.erst.delis.task.document.parse.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;

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

	private DocumentSBDHeader sbdh;
	
	public String toFormatDescription() {
		StringBuilder sb = new StringBuilder();
		if (this.getRoot() != null) {
			sb.append(this.getRoot().getNamespace());
			sb.append("::");
			sb.append(this.getRoot().getRootTag());
		} else {
			sb.append("Failed to parse root tag");
		}
		sb.append(", CustomizationID=");
		sb.append(this.getCustomizationID());
		sb.append(", Profile=");
		if (this.getProfile() !=null) {
			sb.append(this.getProfile().getId());
			if (StringUtils.isNotBlank(this.getProfile().getSchemeID())) {
				sb.append(" schemeId=");
				sb.append(this.getProfile().getSchemeID());
			}
			if (StringUtils.isNotBlank(this.getProfile().getSchemeAgencyId())) {
				sb.append(" schemeAgencyId=");
				sb.append(this.getProfile().getSchemeAgencyId());
			}
		}
		return sb.toString();
	}

}
