package dk.erst.delis.web.accesspoint;

import dk.erst.delis.dao.AccessPointDaoRepository;
import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.enums.access.AccessPointType;
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

import javax.sql.rowset.serial.SerialBlob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        accessPointDaoRepository.save(point);
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(accessPointService).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/accesspoint/list")).andDo(print())
                .andExpect(view().name("accesspoint/list"))
                .andExpect(content().string(containsString("aaa/aaa/aaa")));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testSave() throws Exception {
        this.mockMvc.perform(post("/accesspoint/save")
                .param("type", "AS2")
                .param("url", "url")
                .param("certificate", "certificate"))
                .andDo(print())
                .andExpect(view().name("accesspoint/edit"))
                .andExpect(model().attribute("errorMessage", notNullValue()))
                .andExpect(model().attribute("accessPoint", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateNew() throws Exception {
        this.mockMvc.perform(get("/accesspoint/create/0"))
                .andDo(print())
                .andExpect(view().name("accesspoint/edit"))
                .andExpect(model().attribute("errorMessage", nullValue()))
                .andExpect(model().attribute("accessPoint", notNullValue()));
    }


    @Test
    @WithMockUser(username = "delis")
    public void testUpdate() throws Exception {
        AccessPoint point = new AccessPoint();
        point.setCertificate(new SerialBlob("test1".getBytes()));
        point.setType(AccessPointType.AS4);
        point.setUrl("initial_value");
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

        this.mockMvc.perform(get("/accesspoint/update/" + created.getId()))
                .andDo(print())
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
                .andDo(print())
                .andExpect(view().name("accesspoint/list"))
                .andExpect(model().attribute("accessPointList", notNullValue()));
    }

    @After
    public void cleanUp () {
        accessPointDaoRepository.deleteAll();
    }
}
