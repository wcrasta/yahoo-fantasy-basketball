package com.warrencrasta.fantasy.yahoo.service.core.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.warrencrasta.fantasy.yahoo.domain.league.YahooLeague;
import com.warrencrasta.fantasy.yahoo.domain.season.YahooSeason;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyResponseDTO;
import com.warrencrasta.fantasy.yahoo.mapper.LeagueMapper;
import com.warrencrasta.fantasy.yahoo.mapper.SeasonMapper;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class YahooUserServiceImplTest {

  private final ObjectMapper objectMapper = new ObjectMapper();
  @Mock private YahooClient yahooClient;
  @Mock private SeasonMapper seasonMapper;
  @Mock private LeagueMapper leagueMapper;
  private YahooUserServiceImpl yahooUserService;

  @BeforeEach
  void setUp() {
    yahooUserService = new YahooUserServiceImpl(yahooClient, seasonMapper, leagueMapper);
  }

  @Test
  void testGetSeasonsForUser() throws IOException {
    String resourceUriFragment = "/users;use_login=1/games;game_codes=nba";
    String mockFantasyResponseGamesPath = "src/test/resources/yahoo/nba_games_mock.json";

    FantasyResponseDTO response =
        objectMapper.readValue(new File(mockFantasyResponseGamesPath), FantasyResponseDTO.class);
    List<YahooSeason> yahooSeasons = new ArrayList<>();
    yahooSeasons.add(new YahooSeason("395", "2019"));
    yahooSeasons.add(new YahooSeason("402", "2020"));

    when(yahooClient.getFantasyContent(resourceUriFragment))
        .thenReturn(response.getFantasyContent());
    when(seasonMapper.gameWrapperDTOsToYahooSeasons(anyList())).thenReturn(yahooSeasons);

    List<YahooSeason> seasons = yahooUserService.getSeasonsForUser();
    assertEquals("2020", seasons.get(0).getYear());
    assertEquals("395", seasons.get(1).getId());
  }

  @Test
  void getLeaguesForUser() throws IOException {
    String seasonId = "395";
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("game_key", seasonId);
    String resourceUriFragment = "/users;use_login=1/games;game_keys={game_key}/leagues";
    String mockFantasyResponseLeaguesPath = "src/test/resources/yahoo/nba_leagues_mock.json";

    FantasyResponseDTO response =
        objectMapper.readValue(new File(mockFantasyResponseLeaguesPath), FantasyResponseDTO.class);
    List<YahooLeague> yahooLeagues = new ArrayList<>();
    yahooLeagues.add(new YahooLeague("395.l.37133", "Father Stretch the Floor"));

    when(yahooClient.getFantasyContent(uriVariables, resourceUriFragment))
        .thenReturn(response.getFantasyContent());
    when(leagueMapper.leagueWrapperDTOsToYahooLeagues(anyList())).thenReturn(yahooLeagues);

    List<YahooLeague> leagues = yahooUserService.getLeaguesForUser(seasonId);
    assertEquals("395.l.37133", leagues.get(0).getId());
    assertEquals("Father Stretch the Floor", leagues.get(0).getName());
  }
}
