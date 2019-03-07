package dk.erst.delis.task.identifier.load;

public class OrganizationIdentifierLoadReport {

    private String organizationCode;
    private int add;
    private int update;
    private int delete;
    private int equal;
    private int failed;

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }


    public int getAdd() {
        return add;
    }

    public void setAdd(int add) {
        this.add = add;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public int getDelete() {
        return delete;
    }

    public void setDelete(int delete) {
        this.delete = delete;
    }

    public int getEqual() {
        return equal;
    }

    public void setEqual(int equal) {
        this.equal = equal;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    @Override
    public String toString() {
        return String.format("Organization %s: added %d; updated %d; delete %d; equal %d; failed %d.",
                organizationCode, add, update, delete, equal, failed);
    }
}
