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
        testSFTP(vfsService);
        testFTP(vfsService);
    }

    private void testFTP(VFSService vfsService) throws IOException {
        String url = "ftp://speedtest.tele2.net/";
        String remoteFilePath = "512KB.zip";
        File tempFile = File.createTempFile("vfs-test", ".zip");
        tempFile.deleteOnExit();
        vfsService.download(url, null, null, tempFile.toString(), remoteFilePath);
    }

    private void testSFTP(VFSService vfsService) {
        String username = "tester";
        String password = "password";
        String host = "localhost";
        String remoteFilePath = "/testFile.txt";
        String url = "sftp://" + host;
        URL resource = getClass().getClassLoader().getResource("testFile.txt");
        vfsService.upload(url, username, password, resource.getPath(), remoteFilePath);
        boolean exist = vfsService.exist(url, username, password, remoteFilePath);
        Assert.assertTrue(exist);
        boolean deleted = vfsService.delete(url, username, password, remoteFilePath);
        Assert.assertTrue(deleted);
    }
}
