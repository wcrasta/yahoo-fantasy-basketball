package com.warrencrasta.fantasy.yahoo.controller.rest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import com.warrencrasta.fantasy.yahoo.service.core.livestandings.LiveStandingsService;
import com.warrencrasta.fantasy.yahoo.service.core.powerranking.PowerRankingService;
import com.warrencrasta.fantasy.yahoo.service.core.scoreboard.ScoreboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;


@ExtendWith(MockitoExtension.class)
class LeagueControllerTest {
  private static final String LEAGUE_ID = "395.l.37133";
  private MockMvc mockMvc;
  @Mock private LeagueService leagueService;
  @Mock private LiveStandingsService liveStandingsService;
  @Mock private ScoreboardService scoreboardService;
  @Mock private PowerRankingService powerRankingService;

  @BeforeEach
  void setUp() {
    this.mockMvc = standaloneSetup(
        new LeagueController(leagueService, scoreboardService, powerRankingService, 
            liveStandingsService)).build();
  }

  @Test
  void testGetLeagueInfo() throws Exception {
    String uri = String.format("/leagues/%s/info", LEAGUE_ID);
    mockMvc.perform(get(uri)).andExpect(status().isOk());
  }

  @Test
  void testGetWeeklyMatchups() throws Exception {
    String week = "1";
    String teamId = "395.l.37133.t.14";
    String uri = String.format("/leagues/%s/weekly-matchups", LEAGUE_ID);
    mockMvc
        .perform(get(uri).param("week", week).param("teamId", teamId))
        .andExpect(status().isOk());
  }
}
