package dk.erst.delis.vfs.sftp.service;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class VFSService {

    /**
     * Method to upload a file in Remote server
     *
     * @param url            Server URL
     * @param username       UserName to login
     * @param password       Password to login
     * @param localFilePath  LocalFilePath. Should contain the entire local file path -
     *                       Directory and Filename with \\ as separator
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     */
    public void upload(String url, String username, String password, String localFilePath, String remoteFilePath) {
        System.out.println("VFSService.upload");
        System.out.println("url = [" + url + "], username = [" + username + "], password = [" + password + "], localFilePath = [" + localFilePath + "], remoteFilePath = [" + remoteFilePath + "]");
        File file = new File(localFilePath);
        if (!file.exists())
            throw new RuntimeException("Error. Local file not found");

        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();

            // Create local file object
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            // Create remote file object

            FileSystemOptions fsOptions = createDefaultOptions();
            setAuthenticator(url, username, password, fsOptions);
            FileObject remoteFile = manager.resolveFile(url + remoteFilePath, fsOptions);
            /*
             * use createDefaultOptions() in place of fsOptions for all default
             * options - Ashok.
             */


            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

            System.out.println("File upload success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method to download the file from remote server location
     *
     * @param url            Server URL
     * @param username       UserName to login
     * @param password       Password to login
     * @param localFilePath  LocalFilePath. Should contain the entire local file path -
     *                       Directory and Filename with \\ as separator
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     */
    public void download(String url, String username, String password, String localFilePath, String remoteFilePath) {

        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();

            // Create local file object. Change location if necessary for new downloadFilePath
            FileObject localFile = manager.resolveFile(localFilePath);

            FileSystemOptions fsOptions = createDefaultOptions();
            setAuthenticator(url, username, password, fsOptions);
            // Create remote file object
            FileObject remoteFile = manager.resolveFile(url + remoteFilePath, fsOptions);

            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);

            System.out.println("File download success");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to delete the specified file from the remote system
     *
     * @param url            HostName of the server
     * @param username       UserName to login
     * @param password       Password to login
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     */
    public boolean delete(String url, String username, String password, String remoteFilePath) {
        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();
            FileSystemOptions fsOptions = createDefaultOptions();
            setAuthenticator(url, username, password, fsOptions);
            // Create remote object
            FileObject remoteFile = manager.resolveFile(url + remoteFilePath, fsOptions);
            return remoteFile.exists() && remoteFile.delete();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Check remote file is exist function:

    /**
     * Method to check if the remote file exists in the specified remote
     * location
     *
     * @param url            HostName of the server
     * @param username       UserName to login
     * @param password       Password to login
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     * @return Returns if the file exists in the specified remote location
     */

    public boolean exist(String url, String username, String password, String remoteFilePath) {
        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();
            FileSystemOptions fsOptions = createDefaultOptions();
            setAuthenticator(url, username, password, fsOptions);
            // Create remote object
            FileObject remoteFile = manager.resolveFile(url + remoteFilePath, fsOptions);
            System.out.println("File exist: " + remoteFile.exists());
            return remoteFile.exists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setAuthenticator(String url, String username, String password, FileSystemOptions fsOptions) throws FileSystemException {
        if (username != null && !username.isEmpty() && password != null) {
            StaticUserAuthenticator auth = new StaticUserAuthenticator(url, username, password);
            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fsOptions, auth);
        }
    }

    /**
     * Method to setup default SFTP config
     *
     * @return the FileSystemOptions object containing the specified
     * configuration options
     * @throws FileSystemException
     */
    public FileSystemOptions createDefaultOptions() throws FileSystemException {
        // Create SFTP options
        FileSystemOptions opts = new FileSystemOptions();

        // SSH Key checking
        SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

        /*
         * Using the following line will cause VFS to choose File System's Root
         * as VFS's root. If I wanted to use User's home as VFS's root then set
         * 2nd method parameter to "true"
         */
        // Root directory set to user home
        SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

        // Timeout is count by Milliseconds
        SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);
        return opts;
    }

    private StandardFileSystemManager createFileSystemManager() {
        return new StandardFileSystemManager();
    }
}
