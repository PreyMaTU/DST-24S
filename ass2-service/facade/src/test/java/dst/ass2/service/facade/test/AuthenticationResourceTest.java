package dst.ass2.service.facade.test;

import dst.ass2.service.facade.ServiceFacadeApplication;
import dst.ass2.service.facade.ServiceFacadeApplicationConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ServiceFacadeApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("AuthenticationResourceTest")
public class AuthenticationResourceTest {

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate;
    private HttpHeaders headers;

    @Before
    public void setUp() {
        headers = new HttpHeaders();
        restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(requestFactory);
        requestFactory.setOutputStreaming(false);
        restTemplate.setRequestFactory(bufferingClientHttpRequestFactory);
    }

    @Test
    public void authenticate_withValidUser_returnsOkAndToken() throws Exception {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = url("/auth/authenticate");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("email", "junit@example.com");
        body.add("password", "junit");

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
        assertNotNull(response.getBody());
        assertThat(response.getBody(), is(ServiceFacadeApplicationConfig.MockAuthenticationClient.TOKEN));
    }

    @Test
    public void authenticate_withInvalidUser_returnsAppropriateCode() throws Exception {
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = url("/auth/authenticate");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("email", "nonexisting@example.com");
        body.add("password", "wrong");

        HttpEntity<?> request = new HttpEntity<>(body, headers);
        HttpStatus status;

        try {
            status = restTemplate.postForEntity(url, request, String.class).getStatusCode();
        } catch (HttpClientErrorException e) {
            status = e.getStatusCode();
        }

        assertThat("Return an appropriate error code", status, allOf(
            not(HttpStatus.OK),
            not(HttpStatus.NOT_FOUND),
            not(HttpStatus.INTERNAL_SERVER_ERROR)
        ));
    }

    private String url(String uri) {
        return "http://localhost:" + port + uri;
    }

}
