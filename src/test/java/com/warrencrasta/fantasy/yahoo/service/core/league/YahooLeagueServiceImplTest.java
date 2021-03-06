package com.warrencrasta.fantasy.yahoo.service.core.league;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.team.YahooTeam;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyResponseDTO;
import com.warrencrasta.fantasy.yahoo.dto.internal.LeagueInfoDTO;
import com.warrencrasta.fantasy.yahoo.mapper.TeamMapper;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YahooLeagueServiceImplTest {

  private static final String LEAGUE_ID = "395.l.37133";
  private final ObjectMapper objectMapper = new ObjectMapper();
  @Mock private YahooClient yahooClient;
  @Mock private TeamMapper teamMapper;
  private Map<String, String> uriVariables;
  private YahooLeagueServiceImpl yahooLeagueService;

  @BeforeEach
  void init() {
    yahooLeagueService = new YahooLeagueServiceImpl(yahooClient, teamMapper);
    uriVariables = new HashMap<>();
    uriVariables.put("league_key", LEAGUE_ID);
  }

  @Test
  void testGetLeagueInfo() throws IOException {
    String yahooEndpoint = "/league/{league_key}/teams";
    String mockFantasyResponseTeamsPath =
        "src/test/resources/yahoo/fantasy_response_teams_mock.json";
    String mockYahooTeamsPath = "src/test/resources/yahoo/teams_mock.json";

    FantasyResponseDTO response =
        objectMapper.readValue(new File(mockFantasyResponseTeamsPath), FantasyResponseDTO.class);
    List<YahooTeam> yahooTeams =
        objectMapper.readValue(new File(mockYahooTeamsPath), new TypeReference<>() {});

    when(yahooClient.getFantasyContent(uriVariables, yahooEndpoint))
        .thenReturn(response.getFantasyContent());
    when(teamMapper.teamWrapperDTOsToYahooTeams(anyList())).thenReturn(yahooTeams);

    LeagueInfoDTO leagueInfoDTO = yahooLeagueService.getLeagueInfo(LEAGUE_ID);

    assertEquals(14, leagueInfoDTO.getTeams().size());
    assertEquals(19, leagueInfoDTO.getWeeks().size());
  }

  @Test
  void testGetRelevantCategories() throws IOException {
    String yahooEndpoint = "/league/{league_key}/settings";
    String mockFantasyResponseSettingsPath =
        "src/test/resources/yahoo/fantasy_response_settings_mock.json";

    FantasyResponseDTO response =
        objectMapper.readValue(new File(mockFantasyResponseSettingsPath), FantasyResponseDTO.class);

    when(yahooClient.getFantasyContent(uriVariables, yahooEndpoint))
        .thenReturn(response.getFantasyContent());

    List<StatCategory> relevantCategories = yahooLeagueService.getRelevantCategories(LEAGUE_ID);
    List<String> relevantCategoriesNames =
        relevantCategories.stream().map(StatCategory::getName).collect(Collectors.toList());

    assertEquals(9, relevantCategories.size());
    assertEquals(
        Arrays.asList("FG%", "FT%", "3PTM", "PTS", "REB", "AST", "ST", "BLK", "TO"),
        relevantCategoriesNames);
  }
}
