package dk.erst.delis.rest;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.dao.ConfigValueDaoRepository;
import dk.erst.delis.dao.DocumentBytesDaoRepository;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class RestDocumentControllerConfig {

    @Autowired
    DocumentBytesDaoRepository documentBytesDaoRepository;

    @Bean
    @Primary
    public DocumentBytesStorageService documentBytesStorageService () {
        return new DocumentBytesStorageService(getConfigBean(), documentBytesDaoRepository);
    }

    @Bean
    @Primary
    public ConfigBean getConfigBean () {
        return new MyConfigBean();
    }

    private class MyConfigBean extends ConfigBean {
        Path root = null;
        Path loaded = null;
        Path failed = null;

        public MyConfigBean() {
            super(Mockito.mock(ConfigValueDaoRepository.class));
            try {
                root = Files.createTempDirectory("testRootDelis");
                loaded = Files.createTempDirectory("testLoadDelis");
                failed = Files.createTempDirectory("testFailedDdelis");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Path getStorageLoadedPath() {
            return loaded;
        }

        @Override
        public Path getDocumentRootPath() {
            return root;
        }

        @Override
        public Path getStorageFailedPath() {
            return failed;
        }
    }
}
