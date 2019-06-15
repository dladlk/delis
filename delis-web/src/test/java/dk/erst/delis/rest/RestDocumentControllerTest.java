package dk.erst.delis.rest;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;

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

@RunWith(SpringRunner.class)
@SpringBootTest
@Import(RestDocumentControllerConfig.class)
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class RestDocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUploadWrongFile() throws Exception {
        MockMultipartFile secondFile = new MockMultipartFile("file", "test2121.xml", "text/xml", "<xml>test</xml>".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/document/upload")
                .file(secondFile)
                .param("validateImmediately", "true"))
                .andExpect(redirectedUrlPattern("/document/view/*"))
        ;
    }

    @After
    public void cleanUp () {

    }
}
