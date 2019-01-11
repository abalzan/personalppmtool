package com.andrei.ppmtool.ppmtool;


import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.hamcrest.MatcherAssert;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@AutoConfigureMockMvc
public abstract class BaseTestContext {

    private static final int HTTP_STATUS_CREATED = HttpStatus.CREATED.value();
    private static final int HTTP_STATUS_BAD_REQUEST = HttpStatus.BAD_REQUEST.value();
    private static final int HTTP_STATUS_NO_CONTENT = HttpStatus.NO_CONTENT.value();
    private static final int HTTP_STATUS_NOT_FOUND = HttpStatus.NOT_FOUND.value();

    private static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_USERNAME = "user";
    private static final String AUTH_PASSWORD = "password";

    @Autowired
    protected MockMvc mockMvc;

    private MockHttpServletResponse response;

    protected static void assertResponseJsonEqualsExpectedJson(String expectedJson, ServletResponse response, String actualJson) throws JSONException {
        MatcherAssert.assertThat(response.getContentType(), is(MediaType.APPLICATION_JSON_UTF8_VALUE));
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    protected MockHttpServletResponse sendPutRequestWithJson(String requestUri, String content) throws Exception {
        return mockMvc.perform(put(requestUri).headers(createAuthenticationHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andDo(print())
                .andReturn()
                .getResponse();
    }

    protected MockHttpServletResponse sendPostRequestWithJson(String requestUri, String content) throws Exception {
        return mockMvc.perform(post(requestUri).headers(createAuthenticationHeader())
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andDo(print())
                .andReturn()
                .getResponse();
    }

    protected MockHttpServletResponse sendGetRequest(String requestUri) throws Exception {
        return mockMvc.perform(get(requestUri).headers(createAuthenticationHeader()))
                .andDo(print())
                .andReturn()
                .getResponse();
    }

    protected MockHttpServletResponse sendDeleteRequest(String requestUri) throws Exception {
        return mockMvc.perform(delete(requestUri).headers(createAuthenticationHeader()))
                .andDo(print())
                .andReturn()
                .getResponse();
    }

    protected String getJsonAsString(String jsonFilePath) throws IOException {
        return FileUtils.readFileToString(new ClassPathResource(jsonFilePath).getFile(), "UTF-8");
    }

    protected void assertResponseStatusIsCreated() {
        assertThat(response.getStatus(), is(HTTP_STATUS_CREATED));
    }

    protected void assertResponseStatusIsBadRequest() {
        assertThat(response.getStatus(), is(HTTP_STATUS_BAD_REQUEST));
    }

    protected void assertResponseStatusIsNoContent() {
        assertThat(response.getStatus(), is(HTTP_STATUS_NO_CONTENT));
    }

    protected void assertResponseStatusIsNotFound() {
        assertThat(response.getStatus(), is(HTTP_STATUS_NOT_FOUND));
    }

    protected void assertResponseLocationHeaderIsNotNull() {
        MatcherAssert.assertThat(response.getHeaderValue("Location"), notNullValue());
    }

    protected void assertResponseJsonEqualsExpectedJson(String expectedJson, String actualJson) throws JSONException {
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    private HttpHeaders createAuthenticationHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTH_HEADER_NAME, createJWTAuthToken(AUTH_USERNAME, AUTH_PASSWORD));
        return headers;
    }

    private String createJWTAuthToken(String username, String password) {
        String auth = String.join(":", username, password);
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
        return "Bearer " + new String(encodedAuth);
    }
}
