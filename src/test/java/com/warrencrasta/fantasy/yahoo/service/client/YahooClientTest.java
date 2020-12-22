package com.warrencrasta.fantasy.yahoo.service.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyContentDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.GameWrapperDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class YahooClientTest {

  public static MockWebServer mockBackEnd;
  private YahooClient yahooClient;

  @BeforeAll
  static void setUp() throws IOException {
    mockBackEnd = new MockWebServer();
    mockBackEnd.start();
  }

  @AfterAll
  static void tearDown() throws IOException {
    mockBackEnd.shutdown();
  }

  @BeforeEach
  void initialize() {
    this.yahooClient =
        new YahooClient(WebClient.builder().build(), mockBackEnd.url("/").toString());
  }

  @Test
  void testGetFantasyResponse() throws InterruptedException, IOException {
    String resourceUriFragment = "/users;use_login=1/games;game_codes=nba";
    String mockFantasyResponseGamesPath = "src/test/resources/yahoo/nba_games_mock.json";

    MockResponse mockResponse =
        new MockResponse()
            .addHeader("Content-Type", "application/json; charset=utf-8")
            .setBody(new String(Files.readAllBytes(Paths.get(mockFantasyResponseGamesPath))));

    mockBackEnd.enqueue(mockResponse);

    FantasyContentDTO fantasyContent = yahooClient.getFantasyContent(resourceUriFragment);
    List<GameWrapperDTO> gameWrapperDTOs =
        fantasyContent.getUsers().get(0).getUser().getGames();
    assertEquals(2, gameWrapperDTOs.size());

    RecordedRequest request = mockBackEnd.takeRequest();
    assertEquals("/users;use_login=1/games;game_codes=nba?format=json_f", request.getPath());
  }
}
