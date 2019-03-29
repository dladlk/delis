package dk.erst.delis.vfs.sftp;

import dk.erst.delis.vfs.sftp.service.VFSService;
import org.junit.Assert;
import org.junit.Test;

public class VFSServiceTest {
    //Test local SFTP server available here https://labs.rebex.net/tiny-sftp-server
    @Test
    public void test() {
        VFSService vfsService = new VFSService();
        testSFTP(vfsService);
        testFTP(vfsService);
    }

    private void testFTP(VFSService vfsService) {
        String url = "ftp://speedtest.tele2.net/";
        String remoteFilePath = "512KB.zip";
        vfsService.download(url, null, null, "D:\\512KB.zip", remoteFilePath);
    }

    private void testSFTP(VFSService vfsService) {
        String username = "tester";
        String password = "password";
        String host = "localhost";
        String remoteFilePath = "/testFile.txt";
        String url = "sftp://" + host;
        vfsService.upload(url, username, password, "D:\\testFile.txt", remoteFilePath);
        boolean exist = vfsService.exist(url, username, password, remoteFilePath);
        Assert.assertTrue(exist);
        boolean deleted = vfsService.delete(url, username, password, remoteFilePath);
        Assert.assertTrue(deleted);
    }
}
