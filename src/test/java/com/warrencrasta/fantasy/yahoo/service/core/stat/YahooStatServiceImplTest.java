package com.warrencrasta.fantasy.yahoo.service.core.stat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.stat.TeamStatCategory;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyResponseDTO;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YahooStatServiceImplTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  @Mock private YahooClient yahooClient;

  private YahooStatServiceImpl yahooStatService;

  @BeforeEach
  void setUp() {
    yahooStatService = new YahooStatServiceImpl(yahooClient);
  }

  @Test
  void testGetAllTeamsStats() throws IOException {
    String leagueId = "395.l.37133";
    String week = "1";
    String mockRelevantCategoriesPath = "src/test/resources/yahoo/relevant_categories_mock.json";
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    uriVariables.put("week", week);
    String yahooEndpoint = "/league/{league_key}/scoreboard;week={week}";
    String mockFantasyResponseScoreboardPath =
        "src/test/resources/yahoo/fantasy_response_scoreboard_mock.json";
    String mockAllTeamsStatsPath = "src/test/resources/yahoo/all_teams_stats_mock.json";
    List<StatCategory> relevantCategories =
        objectMapper.readValue(new File(mockRelevantCategoriesPath), new TypeReference<>() {});

    FantasyResponseDTO response =
        objectMapper.readValue(
            new File(mockFantasyResponseScoreboardPath), FantasyResponseDTO.class);

    when(yahooClient.getFantasyContent(uriVariables, yahooEndpoint))
        .thenReturn(response.getFantasyContent());

    List<TeamStatCategory> mockedAllTeamsStats =
        objectMapper.readValue(new File(mockAllTeamsStatsPath), new TypeReference<>() {});
    List<TeamStatCategory> allTeamsStats =
        yahooStatService.getAllTeamsStats(leagueId, week, relevantCategories);

    assertEquals(mockedAllTeamsStats, allTeamsStats);
  }
}
