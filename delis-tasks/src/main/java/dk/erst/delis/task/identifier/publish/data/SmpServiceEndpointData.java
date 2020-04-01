package dk.erst.delis.task.identifier.publish.data;

import java.util.Base64;
import java.util.Date;

import javax.persistence.Transient;

import org.apache.commons.codec.binary.StringUtils;

import dk.erst.delis.web.accesspoint.AccessPointService;
import dk.erst.delis.web.accesspoint.AccessPointService.CertificateData;
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
	/*
	 * X509Certificate encoded
	 */
	private byte[] certificate;
	
	@Transient
	private String certificateName;
	
	public String getCertificateBase64() {
		if (certificate != null) {
			return Base64.getEncoder().encodeToString(certificate);
		}
		return null;
	}
	
	public String getCertificateName() {
		if (this.certificateName == null) {
			this.certificateName = parseCertificateName(this.certificate);
		}
		return this.certificateName;
	}

	private String parseCertificateName(byte[] certificateBytes) {
		String parsedName = null;
		if (certificateBytes != null) {
			CertificateData certificateData = AccessPointService.parseCertificateDataByPEM(getCertificateBase64());
			if (certificateData != null) {
				parsedName = certificateData.getCertififcateName();
			}
		}
		if (parsedName == null) {
			parsedName = "Undefined or not parsable";
		}
		return parsedName;
	}

	public boolean isMatch(SmpServiceEndpointData match) {
		if (match != null) {
			boolean res = StringUtils.equals(this.getTransportProfile(), match.getTransportProfile());
			if (res) {
				res = StringUtils.equals(this.getUrl(), match.getUrl());
			}
			if (res) {
				res = StringUtils.equals(this.getCertificateName(), match.getCertificateName());
			}
			return res;
		}
		return false;
	}
	
}
