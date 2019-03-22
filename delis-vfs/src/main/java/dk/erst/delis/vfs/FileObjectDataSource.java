package dk.erst.delis.vfs;

import dk.erst.delis.vfs.exception.FSPluginException;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import javax.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileObjectDataSource implements DataSource {

    private final FileObject file;

    public FileObjectDataSource(final FileObject file) {
        this.file = file;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return file.getContent().getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return file.getContent().getOutputStream();
    }

    @Override
    public String getContentType() {
        try {
            return file.getContent().getContentInfo().getContentType();
        } catch (final FileSystemException e) {
            throw new FSPluginException("Could not retrieve content type from FileObject", e);
        }
    }

    @Override
    public String getName() {
        return file.getName().getBaseName();
    }

}
