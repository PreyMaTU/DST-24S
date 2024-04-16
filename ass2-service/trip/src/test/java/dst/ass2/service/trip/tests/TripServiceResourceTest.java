package dst.ass2.service.trip.tests;

import dst.ass1.jpa.tests.TestData;
import dst.ass2.service.api.match.IMatchingService;
import dst.ass2.service.api.trip.*;
import dst.ass2.service.trip.TripApplication;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TripApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testdata")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TripServiceResourceTest {

    @Autowired
    private TestData testData;

    @LocalServerPort
    private int port;

    @MockBean
    private IMatchingService matchingService;

    private TestRestTemplate restTemplate;
    private HttpHeaders headers;

    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        return new RestTemplate(requestFactory);
    }

    @Before
    public void setUp() {
        this.headers = new HttpHeaders();
        this.restTemplate = new TestRestTemplate();
    }

    private String url(String uri) {
        return "http://localhost:" + port + uri;
    }

    @Test
    public void createTrip_withWrongHttpMethod_returnsError() {
        String url = url("/trips");
        MultiValueMap<String, String> map = getCreateMap(2134L, 2222L, 33L);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class, map);

        assertThat("Response was: " + response, response.getStatusCode().series(), is(HttpStatus.Series.CLIENT_ERROR));
        assertThat(response.getStatusCode(), not(HttpStatus.NOT_FOUND));
    }

    @Test
    public void createTrip_withUnknownRider_returnsNotFoundError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = url("/trips");
        MultiValueMap<String, String> body = getCreateMap(3333L, testData.location1Id, testData.location2Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat("Response was: " + response, response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }


    @Test
    public void createTrip_withValidKeys_returnsTripId() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = url("/trips");
        MultiValueMap<String, String> body = getCreateMap(testData.rider1Id, testData.location1Id, testData.location2Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
        assertThat(response.getBody(), notNullValue());

        try {
            Long.parseLong(response.getBody());
        } catch (NumberFormatException e) {
            throw new AssertionError("Response body of " + url + " should be a Long value (the trip ID)", e);
        }
    }

    @Test
    public void createTrip_withInvalidTrip_andThenGetTrip_hasNoFare() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = url("/trips");
        MultiValueMap<String, String> body = getCreateMap(testData.rider1Id, testData.location1Id, testData.location2Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
        assertThat(response.getBody(), notNullValue());

        try {
            long id = Long.parseLong(response.getBody());
            ResponseEntity<TripDTO> trip = getTrip(id);
            assertThat(trip.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
            assertNull(trip.getBody().getFare());
        } catch (NumberFormatException e) {
            throw new AssertionError("Response body of " + url + " should be a Long value (the trip ID)", e);
        }
    }


    @Test
    public void findTrip_withUnknownKey_returnsNotFoundError() {
        String url = url("/trips/" + (Long) 1338L);
        ResponseEntity<String> trip = restTemplate.getForEntity(url, String.class);
        assertThat(trip.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void findTrip_onCreatedTrip_returnsJsonEntity() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        Long pickup = testData.location5Id;
        Long rider = testData.rider4Id;
        Long destination = testData.location1Id;
        Long trip = testData.trip6Id;
        String url = url("/trips/" + trip);

        ResponseEntity<TripDTO> response = restTemplate.getForEntity(url, TripDTO.class);
        TripDTO tripDTO = response.getBody();

        assertThat(response.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
        assertNotNull(tripDTO);
        assertEquals(trip, tripDTO.getId());
        assertEquals(destination, tripDTO.getDestinationId());
        assertEquals(pickup, tripDTO.getPickupId());
        assertEquals(rider, tripDTO.getRiderId());
        assertEquals(3, tripDTO.getStops().size());
        assertEquals(getTen(), tripDTO.getFare());
    }

    @Test
    public void addStop_onCreatedTrip_returnsOkAndCalculatedFare() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String url = url("/trips/" + testData.trip6Id + "/stops");
        MultiValueMap<String, String> body = body("locationId", testData.location5Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<MoneyDTO> response = restTemplate.postForEntity(url, request, MoneyDTO.class);
        MoneyDTO moneyDTO = response.getBody();

        assertThat(response.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
        assertNotNull(moneyDTO);
        assertEquals(getTen(), moneyDTO);
    }

    @Test
    public void addStop_andThenGetTrip_containsStopInList() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = url("/trips/" + testData.trip6Id + "/stops");
        MultiValueMap<String, String> body = body("locationId", testData.location5Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<MoneyDTO> response = restTemplate.postForEntity(url, request, MoneyDTO.class);
        assertThat(response.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));

        ResponseEntity<TripDTO> tripResponse = getTrip(testData.trip6Id);
        assertThat(tripResponse.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
        assertThat(tripResponse.getBody().getStops(), hasItem(testData.location5Id));
    }

    @Test
    public void addStop_onQueuedTrip_returnsAppropriateError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String url = url("/trips/" + testData.trip10Id + "/stops");
        MultiValueMap<String, String> body = body("locationId", testData.location5Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);

        assertThat("Make use of an appropriate HTTP status code", response.getStatusCode(), allOf(
            CoreMatchers.is(not(HttpStatus.OK)),
            CoreMatchers.is(not(HttpStatus.NOT_FOUND)),
            CoreMatchers.is(not(HttpStatus.INTERNAL_SERVER_ERROR))
        ));
    }

    @Test
    public void addStop_withNonExistingLocation_returnsNotFoundError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Long trip = testData.trip6Id;

        String url = url("/trips/" + trip + "/stops");
        MultiValueMap<String, String> body = body("locationId", 1337L);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void addStop_withLocationAlreadyInStopList_returnsAppropriateError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Long trip = testData.trip6Id;

        String url = url("/trips/" + trip + "/stops");
        MultiValueMap<String, String> body = body("locationId", testData.location2Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);
        assertThat("Make use of an appropriate HTTP status code", response.getStatusCode(), allOf(
            CoreMatchers.is(not(HttpStatus.OK)),
            CoreMatchers.is(not(HttpStatus.NOT_FOUND)),
            CoreMatchers.is(not(HttpStatus.INTERNAL_SERVER_ERROR))
        ));
    }

    @Test
    public void addStop_withInvalidTrip_returnsOk() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        Long trip = testData.trip6Id;

        String url = url("/trips/" + trip + "/stops");
        MultiValueMap<String, String> body = body("locationId", testData.location5Id);
        HttpEntity<?> request = new HttpEntity<>(body, headers);
        ResponseEntity<?> response = restTemplate.postForEntity(url, request, String.class);
        assertThat(response.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void removeStop_onCreatedTrip_returnOk() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        Long trip = testData.trip6Id;
        Long location = testData.location2Id;
        String url = url("/trips/" + trip + "/stops/" + location);

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void removeStop_andThenGetTrip_doesntContainStopInList() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        Long trip = testData.trip6Id;
        Long location = testData.location2Id;
        String url = url("/trips/" + trip + "/stops/" + location);

        restTemplate.delete(url, new HashMap<>());
        ResponseEntity<TripDTO> response = getTrip(trip);
        assertNotNull(response);
        List<Long> ids = response.getBody().getStops();
        assertFalse(ids.contains(location));
        assertTrue(ids.contains(testData.location3Id));
        assertTrue(ids.contains(testData.location4Id));
    }


    @Test
    public void removeStop_onQueuedTrip_returnsAppropriateError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        Long trip = testData.trip10Id;
        Long location = testData.location4Id;
        String url = url("/trips/" + trip + "/stops/" + location);

        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat("Make use of an appropriate HTTP status code", response.getStatusCode(), allOf(
            CoreMatchers.is(not(HttpStatus.OK)),
            CoreMatchers.is(not(HttpStatus.NOT_FOUND)),
            CoreMatchers.is(not(HttpStatus.INTERNAL_SERVER_ERROR))
        ));
    }

    @Test
    public void removeStop_withNonExistingLocation_returnsNotFoundError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        Long trip = testData.trip6Id;
        Long location = 1234L;
        String url = url("/trips/" + trip + "/stops/" + location);

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }


    @Test
    public void removeStop_WithLocationNotInStopsList_returnsAppropriateError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());
        Long trip = testData.trip6Id;
        Long location = testData.location5Id;
        String url = url("/trips/" + trip + "/stops/" + location);

        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat("Make use of an appropriate HTTP status code", response.getStatusCode(), allOf(
            CoreMatchers.is(not(HttpStatus.OK)),
            CoreMatchers.is(not(HttpStatus.NOT_FOUND)),
            CoreMatchers.is(not(HttpStatus.INTERNAL_SERVER_ERROR))
        ));
    }

    @Test
    public void removeStop_withInvalidTrip_returnsOk() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());
        Long trip = testData.trip6Id;
        Long location = testData.location2Id;
        String url = url("/trips/" + trip + "/stops/" + location);

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void confirmTrip_withKnownTrip_shouldReturnSuccessful() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        Long trip = testData.trip6Id;
        String url = url("/trips/" + trip + "/confirm");

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.PATCH,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void confirmTrip_withUnknownTrip_shouldReturnNotFoundError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenReturn(getTen());

        Long trip = 1244L;
        String url = url("/trips/" + trip + "/confirm");

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.PATCH,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void confirmTrip_withInvalidTrip_shouldReturnAppropriateError() throws InvalidTripException {
        when(matchingService.calculateFare(any())).thenThrow(new InvalidTripException());

        Long trip = testData.trip6Id;
        String url = url("/trips/" + trip + "/confirm");

        ResponseEntity<String> response = restTemplate.exchange(
            url,
            HttpMethod.PATCH,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat("Make use of an appropriate HTTP status code", response.getStatusCode(), allOf(
            CoreMatchers.is(not(HttpStatus.OK)),
            CoreMatchers.is(not(HttpStatus.NOT_FOUND)),
            CoreMatchers.is(not(HttpStatus.INTERNAL_SERVER_ERROR))
        ));
    }

    @Test
    public void deleteTrip_withKnownTrip_shouldReturnSuccessful() {
        Long trip = testData.trip6Id;
        String url = url("/trips/" + trip);

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void deleteTrip_withUnknownTrip_shouldReturnNotFoundError() {
        Long trip = 1245L;
        String url = url("/trips/" + trip);

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.DELETE,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void cancelTrip_withKnownKey_shouldReturnSuccessful() {
        Long trip = testData.trip6Id;
        String url = url("/trips/" + trip + "/cancel");

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.PATCH,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void cancelTrip_withUnknownKey_shouldReturnNotFoundError() {
        Long trip = 12545L;
        String url = url("/trips/" + trip + "/cancel");

        ResponseEntity<String> exchange = restTemplate.exchange(
            url,
            HttpMethod.PATCH,
            null,
            (Class<String>) null,
            new HashMap<String, String>()
        );

        assertThat(exchange.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void completeTrip_withKnownTrip_shouldReturnSuccessful() {
        Long trip = testData.trip9Id;
        TripInfoDTO tripInfoDTO = new TripInfoDTO();
        tripInfoDTO.setCompleted(new Date());
        tripInfoDTO.setDistance(100.0);
        tripInfoDTO.setFare(getTen());
        String url = url("/trips/" + trip + "/complete");

        ResponseEntity<String> exchange = restTemplate.postForEntity(
            url,
            tripInfoDTO,
            String.class
        );

        assertThat(exchange.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void completeTrip_withUnknownTrip_shouldReturnNotFoundError() {
        Long trip = 12545L;
        TripInfoDTO tripInfoDTO = new TripInfoDTO();
        tripInfoDTO.setCompleted(new Date());
        tripInfoDTO.setDistance(100.0);
        tripInfoDTO.setFare(getTen());
        String url = url("/trips/" + trip + "/complete");

        ResponseEntity<String> exchange = restTemplate.postForEntity(
            url,
            tripInfoDTO,
            String.class
        );

        assertThat(exchange.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void matchTrip_withKnownTrip_shouldReturnSuccessful() {
        Long trip = testData.trip10Id;
        Long driver = testData.driver4Id + 1L;
        Long vehicle = testData.vehicle1Id;
        String url = url("/trips/" + trip + "/match");

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setVehicleId(vehicle);
        matchDTO.setDriverId(driver);
        matchDTO.setFare(getTen());

        ResponseEntity<String> exchange = restTemplate.postForEntity(
            url,
            matchDTO,
            String.class
        );

        assertThat(exchange.getStatusCode().series(), is(HttpStatus.Series.SUCCESSFUL));
    }

    @Test
    public void matchTrip_withUnavailableDriver_shouldReturnAppropriateError() {
        Long trip = testData.trip10Id;
        Long driver = testData.driver4Id;
        Long vehicle = testData.vehicle1Id;
        String url = url("/trips/" + trip + "/match");

        MatchDTO matchDTO = new MatchDTO();
        matchDTO.setVehicleId(vehicle);
        matchDTO.setDriverId(driver);
        matchDTO.setFare(getTen());

        ResponseEntity<String> response = restTemplate.postForEntity(
            url,
            matchDTO,
            String.class
        );

        assertThat("Make use of an appropriate HTTP status code", response.getStatusCode(), allOf(
            CoreMatchers.is(not(HttpStatus.OK)),
            CoreMatchers.is(not(HttpStatus.NOT_FOUND)),
            CoreMatchers.is(not(HttpStatus.INTERNAL_SERVER_ERROR))
        ));
    }

    private MultiValueMap<String, String> getCreateMap(Long riderId, Long pickupId, Long destinationId) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("riderId", riderId + "");
        map.add("pickupId", pickupId + "");
        map.add("destinationId", destinationId + "");
        return map;
    }


    private MoneyDTO getTen() {
        MoneyDTO moneyDTO = new MoneyDTO();
        moneyDTO.setCurrency("EUR");
        moneyDTO.setValue(BigDecimal.TEN);
        return moneyDTO;
    }

    private MultiValueMap<String, String> body(String key, Object value) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(key, String.valueOf(value));
        return map;
    }

    private ResponseEntity<TripDTO> getTrip(Long id) {
        String url = url("/trips/" + id);
        return restTemplate.getForEntity(url, TripDTO.class);
    }

}
