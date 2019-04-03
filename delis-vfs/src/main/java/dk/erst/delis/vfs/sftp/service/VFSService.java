package dk.erst.delis.vfs.sftp.service;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Service
public class VFSService {

    private FileSystemOptions options;

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
        File file = new File(localFilePath);
        if (!file.exists())
            throw new RuntimeException("Error. Local file not found");

        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();

            // Create local file object
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            // Create remote file object

            FileSystemOptions fsOptions = getOptions();
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

            FileSystemOptions fsOptions = getOptions();
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
            FileSystemOptions fsOptions = getOptions();
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
            FileSystemOptions fsOptions = getOptions();
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
    public FileSystemOptions getOptions() throws FileSystemException {
        if (options == null) {
            options = loadOptions();
        }
        return options;
    }

    private FileSystemOptions loadOptions() {
        FileSystemOptions options = new FileSystemOptions();
        try {
            Method setOptionMethod = options.getClass().getDeclaredMethod("setOption", Class.class, String.class, Object.class);
            setOptionMethod.setAccessible(true);
            NodeList optionsList = getOptionsNodeList();
            for (int i = 0; i < optionsList.getLength(); i++) {
                Node option = optionsList.item(i);
                Object[] args = prepareArguments(option);
                setOptionMethod.invoke(options, args);
            }
        } catch (NoSuchMethodException |
                InstantiationException |
                InvocationTargetException |
                IllegalAccessException |
                SAXException |
                IOException |
                ParserConfigurationException |
                ClassNotFoundException e) {
            e.printStackTrace();
        }
        return options;
    }

    private Object[] prepareArguments(Node option) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        NamedNodeMap attributes = option.getAttributes();
        String name = attributes.getNamedItem("name").getNodeValue();
        String type = attributes.getNamedItem("type").getNodeValue();
        String fileSystemClassName = attributes.getNamedItem("fileSystemClassName").getNodeValue();
        String optionStringValue = option.getTextContent().trim();
        Class<?> typeClass = Class.forName(type);
        Class<?> fileSystemClass = Class.forName(fileSystemClassName);
        Object value = createTypedValue(optionStringValue, typeClass);
        return new Object[]{fileSystemClass, name, value};
    }

    private NodeList getOptionsNodeList() throws ParserConfigurationException, SAXException, IOException {
        InputStream in = getClass().getClassLoader().getResourceAsStream("config.xml");
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(in);
        doc.getDocumentElement().normalize();
        return doc.getDocumentElement().getElementsByTagName("option");
    }

    private Object createTypedValue(String stringValue, Class<?> typeClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        return typeClass.getConstructor(String.class).newInstance(stringValue);
    }

    private StandardFileSystemManager createFileSystemManager() {
        return new StandardFileSystemManager();
    }
}
