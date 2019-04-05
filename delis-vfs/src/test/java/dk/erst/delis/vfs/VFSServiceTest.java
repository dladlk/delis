package dk.erst.delis.vfs;

import dk.erst.delis.vfs.service.VFSService;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class VFSServiceTest {
    //Test local SFTP server available here https://labs.rebex.net/tiny-sftp-server
    @Test
    public void test() throws IOException {
        VFSService vfsService = new VFSService();
        String configSftpPath = getClass().getClassLoader().getResource("config-sftp.xml").getPath();
        String configFtpPath = getClass().getClassLoader().getResource("config-ftp.xml").getPath();
        testSFTP(vfsService, configSftpPath);
        testFTP(vfsService, configFtpPath);
    }

    private void testFTP(VFSService vfsService, String configPath) throws IOException {
        String remoteFilePath = "/512KB.zip";
        File tempFile = File.createTempFile("vfs-test", ".zip");
        tempFile.deleteOnExit();
        vfsService.download(configPath, tempFile.toString(), remoteFilePath);
    }

    private void testSFTP(VFSService vfsService, String configPath) {
        String remoteFilePath = "/testFile.txt";
        URL resource = getClass().getClassLoader().getResource("testFile.txt");
        vfsService.upload(configPath, resource.getPath(), remoteFilePath);
        boolean exist = vfsService.exist(configPath, remoteFilePath);
        Assert.assertTrue(exist);
        boolean deleted = vfsService.delete(configPath, remoteFilePath);
        Assert.assertTrue(deleted);
    }
}
