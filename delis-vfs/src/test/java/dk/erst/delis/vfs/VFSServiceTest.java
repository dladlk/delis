package dk.erst.delis.vfs;

import dk.erst.delis.vfs.service.VFSConfigException;
import dk.erst.delis.vfs.service.VFSService;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class VFSServiceTest {

    private VFSService vfsService = new VFSService();

    //Test local SFTP server available here https://labs.rebex.net/tiny-sftp-server

    @Test
    @Ignore
    public void testSFTPByKey() throws IOException, VFSConfigException {
        if (new File("D:\\test\\sftptest3.ppk").exists()) {
            testSFTPUpload(loadResourcePath("config-sftp-ppk.xml"));
        }
    }

    //todo add checking on sftp local server available
    @Test
    @Ignore
    public void testSFTPUpload() throws VFSConfigException {
        testSFTPUpload(loadResourcePath("config-sftp.xml"));
    }
    
    @Test
    @Ignore
    public void testFileUpload() throws VFSConfigException {
        String configPath = loadResourcePath("config-file.xml");
        String remoteFilePath = "/testFile.txt";
        URL resource = getClass().getClassLoader().getResource("testFile.txt");
        vfsService.upload(configPath, resource.getPath(), remoteFilePath);
        boolean exist = vfsService.exist(configPath, remoteFilePath);
        Assert.assertTrue(exist);
        boolean deleted = vfsService.delete(configPath, remoteFilePath);
        Assert.assertTrue(deleted);
    }

    @Test
    @Ignore
    public void testFTP() throws IOException, VFSConfigException {
        testFTPDownload(loadResourcePath("config-ftp.xml"));
    }

    private void testFTPDownload(String configPath) throws IOException, VFSConfigException {
        String remoteFilePath = "/512KB.zip";
        File tempFile = File.createTempFile("vfs-test", ".zip");
        tempFile.deleteOnExit();
        vfsService.download(configPath, tempFile.toString(), remoteFilePath);
    }

    private void testSFTPUpload(String configPath) throws VFSConfigException {
        String remoteFilePath = "/testFile.txt";
        URL resource = getClass().getClassLoader().getResource("testFile.txt");
        vfsService.upload(configPath, resource.getPath(), remoteFilePath);
        boolean exist = vfsService.exist(configPath, remoteFilePath);
        Assert.assertTrue(exist);
        boolean deleted = vfsService.delete(configPath, remoteFilePath);
        Assert.assertTrue(deleted);
    }

    private String loadResourcePath(String name) {
        return getClass().getClassLoader().getResource(name).getPath();
    }
}
