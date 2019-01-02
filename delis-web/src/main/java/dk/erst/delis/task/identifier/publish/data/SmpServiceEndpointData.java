package dk.erst.delis.task.identifier.publish.data;

import java.util.Date;

import lombok.Data;

@Data
public class SmpServiceEndpointData {

	private String transportProfile;
	private String url;
	private String technicalContactUrl;
	private String serviceDescription;
	private Date serviceActivationDate;
	private Date serviceExpirationDate;
	private boolean requireBusinessLevelSignature;
	private byte[] certificate;

}
