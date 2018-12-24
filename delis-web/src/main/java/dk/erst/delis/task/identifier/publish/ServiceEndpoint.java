package dk.erst.delis.task.identifier.publish;

import java.util.Date;

public class ServiceEndpoint {

    private String transportProfile;
    private String url;
    private String technicalContactUrl;
    private String serviceDescription;
    private Date serviceActivationDate;
    private Date serviceExpirationDate;
    private boolean requireBusinessLevelSignature;
    private byte[] certificate;

    public String getTransportProfile() {
        return transportProfile;
    }

    public void setTransportProfile(String transportProfile) {
        this.transportProfile = transportProfile;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTechnicalContactUrl() {
        return technicalContactUrl;
    }

    public void setTechnicalContactUrl(String technicalContactUrl) {
        this.technicalContactUrl = technicalContactUrl;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public Date getServiceActivationDate() {
        return serviceActivationDate;
    }

    public void setServiceActivationDate(Date serviceActivationDate) {
        this.serviceActivationDate = serviceActivationDate;
    }

    public Date getServiceExpirationDate() {
        return serviceExpirationDate;
    }

    public void setServiceExpirationDate(Date serviceExpirationDate) {
        this.serviceExpirationDate = serviceExpirationDate;
    }

    public boolean isRequireBusinessLevelSignature() {
        return requireBusinessLevelSignature;
    }

    public void setRequireBusinessLevelSignature(boolean requireBusinessLevelSignature) {
        this.requireBusinessLevelSignature = requireBusinessLevelSignature;
    }

    public byte[] getCertificate() {
        return certificate;
    }

    public void setCertificate(byte[] certificate) {
        this.certificate = certificate;
    }
}
