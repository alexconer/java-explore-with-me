package ru.practicum.explorewithme.statclient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import ru.practicum.explorewithme.statclient.client.StatClient;


import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@ActiveProfiles("test")
@SpringBootTest
public class StatClientTest {

    private StatClient statClient;

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp(@Value("${stat-server.url}") String baseUrl) {
        statClient = new StatClient(baseUrl, new RestTemplateBuilder());
    }

    @Test
    void postRequestTest() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.created(new URI("/hit")).build();

        ResponseEntity<Object> actualResponse = statClient.save("test-app", "/test", "127.0.0.1");

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }

    @Test
    void getRequestTest() throws Exception {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        ResponseEntity<Object> actualResponse = statClient.getStats(LocalDateTime.now().minusMinutes(1), LocalDateTime.now(), new String[]{"/test"}, true);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getStatusCode(), actualResponse.getStatusCode());
    }
}
