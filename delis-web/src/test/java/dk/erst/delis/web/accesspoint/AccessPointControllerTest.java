package dk.erst.delis.web.accesspoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.sql.rowset.serial.SerialBlob;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import dk.erst.delis.dao.AccessPointDaoRepository;
import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.enums.access.AccessPointType;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AccessPointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccessPointService accessPointService;

    @Autowired
    private AccessPointDaoRepository accessPointDaoRepository;

    @Before
    public void setUp() throws Exception {
        AccessPoint point = new AccessPoint();
        point.setCertificate(new SerialBlob("test".getBytes()));
        point.setType(AccessPointType.AS4);
        point.setUrl("aaa/aaa/aaa");
        point.setServiceDescription("s_d");
        point.setTechnicalContactUrl("ttt/ttt/ttt");
        accessPointDaoRepository.save(point);
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(accessPointService).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/accesspoint/list"))
                .andExpect(view().name("accesspoint/list"))
                .andExpect(content().string(containsString("aaa/aaa/aaa")))
                .andExpect(content().string(containsString("s_d")))
                .andExpect(content().string(containsString("ttt/ttt/ttt")));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testSave() throws Exception {
        this.mockMvc.perform(post("/accesspoint/save")
                .param("type", "AS2")
                .param("url", "url")
                .param("serviceDescription", "serviceDescription")
                .param("technicalContactUrl", "technicalContactUrl")
                .param("certificate", "certificate"))
                
                .andExpect(view().name("accesspoint/edit"))
                .andExpect(model().attribute("errorMessage", notNullValue()))
                .andExpect(model().attribute("accessPoint", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateNew() throws Exception {
        this.mockMvc.perform(get("/accesspoint/create"))
                .andExpect(view().name("accesspoint/edit"))
                .andExpect(model().attribute("accessPoint", notNullValue()));
    }


    @Test
    @WithMockUser(username = "delis")
    public void testUpdate() throws Exception {
        AccessPoint point = new AccessPoint();
        point.setCertificate(new SerialBlob("test1".getBytes()));
        point.setType(AccessPointType.AS4);
        point.setUrl("initial_value");
        point.setServiceDescription("s_d");
        point.setTechnicalContactUrl("ttt/ttt/ttt");
        accessPointDaoRepository.save(point);

        Iterable<AccessPoint> all = accessPointDaoRepository.findAll();
        AccessPoint created = null;
        for (AccessPoint existingPoint : all) {
            if (existingPoint.getUrl().equals(point.getUrl())) {
                created = existingPoint;
                break;
            }
        }

        Assert.assertNotNull(created);
        Assert.assertEquals(created.getServiceDescription(), "s_d");
        Assert.assertEquals(created.getTechnicalContactUrl(), "ttt/ttt/ttt");

        this.mockMvc.perform(get("/accesspoint/update/" + created.getId()))
                
                .andExpect(view().name("accesspoint/edit"))
                .andExpect(model().attribute("accessPoint", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testDelete() throws Exception {
        AccessPoint point = new AccessPoint();
        point.setCertificate(new SerialBlob("test2".getBytes()));
        point.setType(AccessPointType.AS4);
        point.setUrl("http://to/delete");
        accessPointDaoRepository.save(point);

        Iterable<AccessPoint> all = accessPointDaoRepository.findAll();
        AccessPoint created = null;
        for (AccessPoint existingPoint : all) {
            if (existingPoint.getUrl().equals(point.getUrl())) {
                created = existingPoint;
                break;
            }
        }

        Assert.assertNotNull(created);

        this.mockMvc.perform(get("/accesspoint/delete/" + created.getId()))
                .andExpect(view().name("redirect:/accesspoint/list"));
    }

    @After
    public void cleanUp () {
        accessPointDaoRepository.deleteAll();
    }
}
