package dk.erst.delis.rest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dk.erst.delis.property.PropertyOverrideContextInitializer;
import dk.erst.delis.util.ConstValues;
import dk.erst.delis.dao.UserRepository;
import dk.erst.delis.data.entities.user.User;
import dk.erst.delis.rest.data.response.auth.AuthData;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = PropertyOverrideContextInitializer.class)
public class AllRestControllerTest {

    @Value("${security.client.id}") String clientId;
    @Value("${security.client.secret}") String clientSecret;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

    private CopyOnWriteArrayList<AuthData> authDataList = new CopyOnWriteArrayList<>();

    @Before
    public void initUser() {
        for (int i = 0; i < ConstValues.paths.size(); i++) {
            User user = userRepository.findByUsernameIgnoreCase(ConstValues.USERNAME + i);
            if (user == null) {
                user = new User();
                user.setUsername(ConstValues.USERNAME + i);
                user.setPassword(new BCryptPasswordEncoder().encode(ConstValues.PASSWORD));
                userRepository.save(user);
            }
        }
    }

    @Test
    public void loginProcessAndTestRequest() {
        log.info("start test for multiThread request");
        ExecutorService threadPool = Executors.newFixedThreadPool(ConstValues.paths.size());
        List<Future<Boolean>> futures = new ArrayList<>();
        long start = System.nanoTime();

        for (int i = 0; i < ConstValues.paths.size(); i++) {
            final int j = i;
            futures.add(CompletableFuture.supplyAsync(() -> loginProcessAndTestRequest(j), threadPool));
        }
        for (Future<Boolean> future : futures) {
            try {
                Assert.assertTrue(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("thread execution failed: " + e.getMessage());
            }
        }

        log.info(String.format("Executed by %d s.", (System.nanoTime() - start) / (1000_000_000)));
        threadPool.shutdown();
        log.info("end test for multiThread request");
    }

    private boolean loginProcessAndTestRequest(int suffix) {
        loginProcess(suffix);
        if (loginProcess(suffix)) {
            delisRestApiTest(suffix, ConstValues.paths.get(suffix));
            return true;
        }
        return false;
    }

    private boolean loginProcess(int suffix) {
        log.info("start login process");
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(ConstValues.GRANT_TYPE_PARAM, ConstValues.GRANT_TYPE);
        params.add(ConstValues.USERNAME_PARAM, ConstValues.USERNAME + suffix);
        params.add(ConstValues.PASSWORD_PARAM, ConstValues.PASSWORD);

        ResultActions result;
        try {
            result = mockMvc.perform(post(ConstValues.OAUTH_REQUEST)
                    .params(params)
                    .with(httpBasic(clientId, clientSecret))
                    .accept(ConstValues.CONTENT_TYPE))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(ConstValues.CONTENT_TYPE));
        } catch (Exception e) {
            ConstValues.mockMvcIsNotPerformingMessage(e.getMessage(), suffix);
            return false;
        }

        String resultString;
        try {
            resultString = result.andReturn().getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            log.error("problem write to response: " + e.getMessage());
            return false;
        }
        String data = new JacksonJsonParser().parseMap(resultString).get("data").toString();
        Type type = new TypeToken<AuthData>() {}.getType();
        authDataList.add(new Gson().fromJson(data, type));
        log.info("end login process");
        return true;
    }

    private void delisRestApiTest(int position, String path) {
        log.info("start delis rest api test");
        MvcResult mvcResult = null;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders
                    .get(path)
                    .header(ConstValues.AUTHORIZATION_HEADER, ConstValues.BEARER_PARAM + authDataList.get(position).getAccessToken()))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
        } catch (Exception e) {
            ConstValues.mockMvcIsNotPerformingMessage(e.getMessage(), position);
        }
        if (mvcResult != null) {
            assertEquals(ConstValues.SUCCESSFUL_STATUS, mvcResult.getResponse().getStatus());
        } else {
            ConstValues.isRequestFailedMessage(position);
        }
        log.info("end start delis rest api test");
    }
}
