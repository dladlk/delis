package dk.erst.delis.task.identifier.publish;

public class PublishReport {

    private boolean serviceGroupCreated;
    private boolean serviceMetadataCreated;

    public PublishReport(boolean serviceGroupCreated, boolean serviceMetadataCreated) {
        this.serviceGroupCreated = serviceGroupCreated;
        this.serviceMetadataCreated = serviceMetadataCreated;
    }

    public boolean isServiceGroupCreated() {
        return serviceGroupCreated;
    }

    public boolean isServiceMetadataCreated() {
        return serviceMetadataCreated;
    }
}
