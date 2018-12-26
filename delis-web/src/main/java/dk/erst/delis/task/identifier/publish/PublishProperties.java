package dk.erst.delis.task.identifier.publish;

import java.util.List;

public class PublishProperties {

    private String participantIdentifierScheme;
    private String participantIdentifierValue;

    private String documentIdentifierScheme;
    private String documentIdentifierValue;

    private String processIdentifierScheme;
    private String processIdentifierValue;

    private List<ServiceEndpoint> endpoints;

    public String getParticipantIdentifierScheme() {
        return participantIdentifierScheme;
    }

    public void setParticipantIdentifierScheme(String participantIdentifierScheme) {
        this.participantIdentifierScheme = participantIdentifierScheme;
    }

    public String getParticipantIdentifierValue() {
        return participantIdentifierValue;
    }

    public void setParticipantIdentifierValue(String participantIdentifierValue) {
        this.participantIdentifierValue = participantIdentifierValue;
    }

    public String getDocumentIdentifierScheme() {
        return documentIdentifierScheme;
    }

    public void setDocumentIdentifierScheme(String documentIdentifierScheme) {
        this.documentIdentifierScheme = documentIdentifierScheme;
    }

    public String getDocumentIdentifierValue() {
        return documentIdentifierValue;
    }

    public void setDocumentIdentifierValue(String documentIdentifierValue) {
        this.documentIdentifierValue = documentIdentifierValue;
    }

    public String getProcessIdentifierScheme() {
        return processIdentifierScheme;
    }

    public void setProcessIdentifierScheme(String processIdentifierScheme) {
        this.processIdentifierScheme = processIdentifierScheme;
    }

    public String getProcessIdentifierValue() {
        return processIdentifierValue;
    }

    public void setProcessIdentifierValue(String processIdentifierValue) {
        this.processIdentifierValue = processIdentifierValue;
    }

    public List<ServiceEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<ServiceEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
