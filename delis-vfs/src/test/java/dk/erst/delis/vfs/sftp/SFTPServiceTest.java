package dk.erst.delis.vfs.sftp;

import dk.erst.delis.vfs.sftp.service.SFTPService;
import org.junit.Assert;
import org.junit.Test;

public class SFTPServiceTest {
    //Test local SFTP server available here https://labs.rebex.net/tiny-sftp-server
    @Test
    public void test() {
        SFTPService sftpService = new SFTPService();
        String username = "tester";
        String password = "password";
        String host = "localhost";
        String remoteFilePath = "OUT/testFile.txt";
        String uri = "sftp://" + username + ":" + password + "@" + host + "/" + remoteFilePath;
        sftpService.upload(uri, "D:\\testFile.txt");
        boolean exist = sftpService.exist(uri);
        Assert.assertTrue(exist);
        boolean deleted = sftpService.delete(uri);
        Assert.assertTrue(deleted);

    }
}
