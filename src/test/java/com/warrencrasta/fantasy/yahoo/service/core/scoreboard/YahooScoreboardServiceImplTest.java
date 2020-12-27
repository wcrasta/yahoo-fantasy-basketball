package com.warrencrasta.fantasy.yahoo.service.core.scoreboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.stat.TeamStatCategory;
import com.warrencrasta.fantasy.yahoo.dto.internal.MatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import com.warrencrasta.fantasy.yahoo.service.core.stat.StatService;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YahooScoreboardServiceImplTest {
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Mock private LeagueService leagueService;
  @Mock private StatService statService;
  private YahooScoreboardServiceImpl yahooScoreboardService;

  @BeforeEach
  void init() {
    yahooScoreboardService = new YahooScoreboardServiceImpl(leagueService, statService);
  }

  @Test
  void testGetWeeklyMatchups() throws IOException {
    String leagueId = "395.l.37133";
    String week = "1";
    String teamId = "395.l.37133.t.14";
    String mockRelevantCategoriesPath = "src/test/resources/yahoo/relevant_categories_mock.json";
    String mockAllTeamsStatsPath = "src/test/resources/yahoo/all_teams_stats_mock.json";
    String mockWeeklyMatchupsPath = "src/test/resources/yahoo/weekly_matchups_mock.json";

    List<StatCategory> mockRelevantCategories =
        objectMapper.readValue(new File(mockRelevantCategoriesPath), new TypeReference<>() {});
    List<TeamStatCategory> mockedAllTeamsStats =
        objectMapper.readValue(new File(mockAllTeamsStatsPath), new TypeReference<>() {});

    when(leagueService.getRelevantCategories(leagueId)).thenReturn(mockRelevantCategories);
    when(statService.getAllTeamsStats(leagueId, week, mockRelevantCategories))
        .thenReturn(mockedAllTeamsStats);

    List<MatchupDTO> mockedWeeklyMatchups =
        objectMapper.readValue(new File(mockWeeklyMatchupsPath), new TypeReference<>() {});
    List<MatchupDTO> weeklyMatchups =
        yahooScoreboardService.getWeeklyMatchups(leagueId, week, teamId);
    assertEquals(mockedWeeklyMatchups, weeklyMatchups);
  }
}
