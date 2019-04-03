package dk.erst.delis.vfs.sftp.service;

import org.apache.commons.vfs2.FileSystemOptions;

public class VFSConfig {

    private FileSystemOptions fsOptions;
    private String url;

    public VFSConfig(FileSystemOptions fsOptions, String url) {
        this.fsOptions = fsOptions;
        this.url = url;
    }

    public FileSystemOptions getFsOptions() {
        return fsOptions;
    }

    public void setFsOptions(FileSystemOptions fsOptions) {
        this.fsOptions = fsOptions;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
