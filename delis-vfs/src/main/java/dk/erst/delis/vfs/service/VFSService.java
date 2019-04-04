package dk.erst.delis.vfs.service;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Service
public class VFSService {

    private static final Logger log = LoggerFactory.getLogger(VFSService.class);
    private Map<File, VFSConfig> configMap = new HashMap<>();

    /**
     * Method to upload a file in Remote server
     * @param config         Config file path
     * @param localFilePath  LocalFilePath. Should contain the entire local file path -
     *                       Directory and Filename with \\ as separator
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     */
    public void upload(String config, String localFilePath, String remoteFilePath) {
        File file = new File(localFilePath);
        if (!file.exists())
            throw new RuntimeException("Error. Local file not found");

        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());
            VFSConfig vfsConfig = getConfig(config);
            FileSystemOptions fsOptions = vfsConfig.getFsOptions();
            String baseUrl = vfsConfig.getUrl();
            FileObject remoteFile = manager.resolveFile(baseUrl + remoteFilePath, fsOptions);
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);
            log.info(String.format("File '%s' successfully uploaded to '%s'", file.getPath(), remoteFile.getName().getPath()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Method to download the file from remote server location
     *
     * @param localFilePath  LocalFilePath. Should contain the entire local file path -
     *                       Directory and Filename with \\ as separator
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     */
    public void download(String config, String localFilePath, String remoteFilePath) {
        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();
            // Create local file object. Change location if necessary for new downloadFilePath
            FileObject localFile = manager.resolveFile(localFilePath);
            VFSConfig vfsConfig = getConfig(config);
            FileSystemOptions fsOptions = vfsConfig.getFsOptions();
            String baseUrl = vfsConfig.getUrl();
            FileObject remoteFile = manager.resolveFile(baseUrl + remoteFilePath, fsOptions);
            localFile.copyFrom(remoteFile, Selectors.SELECT_SELF);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to delete the specified file from the remote system
     *
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     */
    public boolean delete(String config, String remoteFilePath) {
        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();
            VFSConfig vfsConfig = getConfig(config);
            FileSystemOptions fsOptions = vfsConfig.getFsOptions();
            String baseUrl = vfsConfig.getUrl();
            FileObject remoteFile = manager.resolveFile(baseUrl + remoteFilePath, fsOptions);
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
     * @param remoteFilePath remoteFilePath. Should contain the entire remote file path -
     *                       Directory and Filename with / as separator
     * @return Returns if the file exists in the specified remote location
     */

    public boolean exist(String config, String remoteFilePath) {
        try (StandardFileSystemManager manager = createFileSystemManager()) {
            manager.init();
            VFSConfig vfsConfig = getConfig(config);
            FileSystemOptions fsOptions = vfsConfig.getFsOptions();
            String baseUrl = vfsConfig.getUrl();
            FileObject remoteFile = manager.resolveFile(baseUrl + remoteFilePath, fsOptions);
            return remoteFile.exists();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setAuthenticator(Element urlElement, FileSystemOptions fsOptions) throws FileSystemException {
        String username = urlElement.getAttribute("username");
        if (StringUtils.isNotBlank(username)) {
            String url = urlElement.getTextContent();
            String password = urlElement.getAttribute("password");
            StaticUserAuthenticator auth = new StaticUserAuthenticator(url, username, password);
            DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fsOptions, auth);
        }
    }

    private VFSConfig getConfig(String configFilePath) throws IOException {
        File configFile = new File(configFilePath);
        VFSConfig config = configMap.get(configFile);
        if (config == null) {
            config = loadConfig(configFile);
            configMap.put(configFile, config);
        }
        return config;
    }

    private VFSConfig loadConfig(File configFile) {
        String configFilePath = configFile.getAbsolutePath();
        try {
            log.info(String.format("Loading service config from file '%s' ...", configFilePath));
            FileSystemOptions options = new FileSystemOptions();
            Document document = createDocument(configFile);
            NodeList fsOptionsList = document.getDocumentElement().getElementsByTagName("FileSystemOptions");
            for (int i = 0; i < fsOptionsList.getLength(); i++) {
                Node fsOption = fsOptionsList.item(i);
                String builderClassName = fsOption.getAttributes().getNamedItem("builder").getTextContent();
                Class<?> builderClass = Class.forName(builderClassName);
                setOptions(builderClass, options, ((Element) fsOption).getElementsByTagName("option"));
            }
            Element urlElement = (Element) document.getElementsByTagName("url").item(0);
            String url = urlElement.getTextContent();
            setAuthenticator(urlElement, options);
            log.info(String.format("Service config successfully loaded from file '%s'", configFilePath));
            return new VFSConfig(options, url);
        } catch (Exception e) {
            log.error(String.format("Failed to load config from file '%s'", configFilePath), e);
        }
        return null;
    }

    private void setOptions(Class<?> builderClass, FileSystemOptions options, NodeList optionNodes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        final String methodNamePrefix = "set";
        for (int i = 0; i < optionNodes.getLength(); i++) {
            Element optionNode = (Element) optionNodes.item(i);
            String optionName = optionNode.getAttribute("name");
            String optionValue = optionNode.getTextContent().trim();
            String methodName = methodNamePrefix + optionName;
            Method optionSetter = findMethodByName(builderClass, methodName);
            Class<?>[] parameterTypes = optionSetter.getParameterTypes();
            Class<?> optionTypeClass = parameterTypes[1];
            optionTypeClass = ClassUtils.primitiveToWrapper(optionTypeClass);
            Object optionValueObject = optionTypeClass.getConstructor(optionValue.getClass()).newInstance(optionValue);
            Object args[] = {options, optionValueObject};
            Object builderInstance = builderClass.getMethod("getInstance").invoke(null);
            optionSetter.invoke(builderInstance, args);
        }
    }

    private Method findMethodByName(Class<?> builderClass, String methodName) throws NoSuchMethodException {
        for (Method method : builderClass.getDeclaredMethods()) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        return null;
    }

    private Document createDocument(File configFile) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document document = docBuilder.parse(new FileInputStream(configFile));
        document.getDocumentElement().normalize();
        return document;
    }

    private StandardFileSystemManager createFileSystemManager() {
        return new StandardFileSystemManager();
    }
}
