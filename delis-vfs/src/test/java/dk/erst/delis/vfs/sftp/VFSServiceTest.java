package dk.erst.delis.vfs.sftp;

import dk.erst.delis.vfs.sftp.service.VFSService;
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
        String configPath = getClass().getClassLoader().getResource("config.xml").getPath();
        testSFTP(vfsService, configPath);
//        testFTP(vfsService, configPath);
    }

    private void testFTP(VFSService vfsService, String configPath) throws IOException {
        String url = "ftp://speedtest.tele2.net/";
        String remoteFilePath = "512KB.zip";
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
