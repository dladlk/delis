package dk.erst.delis.rest;

import dk.erst.delis.config.ConfigBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.file.Path;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(RestDocumentControllerConfig.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class RestDocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ConfigBean configBean;

    @Autowired
    private RestDocumentController controller;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUploadWrongFile() throws Exception {

        String s = controller.toString();
        Path storageLoadedPath = configBean.getStorageLoadedPath();
        System.out.println("storageLoadedPath = " + storageLoadedPath);
        MockMultipartFile secondFile = new MockMultipartFile("file", "test2121.xml", "text/xml", "<xml>test</xml>".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/rest/document/upload")
                .file(secondFile)
                .param("validateImmediately", "true"))
                .andExpect(status().is(400))
        .andExpect(content().string(containsString("Uploaded file as a document")));
    }

    @After
    public void cleanUp () {

    }
}
