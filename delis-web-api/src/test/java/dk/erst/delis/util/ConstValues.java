package dk.erst.delis.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ConstValues {

    public static final String CONTENT_TYPE = "application/json;charset=UTF-8";
    public static final String GRANT_TYPE = "password";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PARAM = "Bearer ";
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String USERNAME_PARAM = "username";
    public static final String PASSWORD_PARAM = "password";
    public static final String USERNAME = "admin";
    public static final String PASSWORD = "admin";
    public static final String OAUTH_REQUEST = "/oauth/token";
    public static final String TEST_REQUEST = "/rest/dashboard";
    public static final int SUCCESSFUL_STATUS = 200;
    public static final int UNAUTHORIZED_STATUS = 401;
    public static final int USER_SIZE = 10;

    public static final List<String> paths = Arrays.asList(
            "/rest/table-info/enums",
            "/rest/table-info/organizations",
            "/rest/document?page=0&size=10&sort=id&order=desc",
            "/rest/document/send?page=0&size=10&sort=id&order=desc",
            "/rest/identifier?page=1&size=10&sort=id&order=desc",
            "/rest/journal/document?page=1&size=10&sort=id&order=desc",
            "/rest/journal/identifier?page=1&size=10&&sort=id&order=desc");

    public static void isNotLoggedInMessage(int position) {
        log.info("user " + ConstValues.USERNAME + position + " is not logged in");
    }

    public static void isLoggedInMessage(int position) {
        log.info("user " + ConstValues.USERNAME + position + " is logged in");
    }

    public static void mockMvcIsNotPerformingMessage(String message, int position) {
        log.error("mockMvc is not performing: " + message + " by user " + ConstValues.USERNAME + position);
    }

    public static void isRequestFailedMessage(int position) {
        log.error("index: " + position + " is request failed");
    }
}
