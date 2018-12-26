package dk.erst.delis.task.identifier.publish;

import java.util.List;

import lombok.Data;

@Data
public class PublishProperties {

    private String documentIdentifierScheme;
    private String documentIdentifierValue;

    private String processIdentifierScheme;
    private String processIdentifierValue;

    private List<ServiceEndpoint> endpoints;

}
