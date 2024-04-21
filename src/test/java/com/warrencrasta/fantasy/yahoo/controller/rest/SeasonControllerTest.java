package com.warrencrasta.fantasy.yahoo.controller.rest;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.warrencrasta.fantasy.yahoo.domain.league.League;
import com.warrencrasta.fantasy.yahoo.domain.league.YahooLeague;
import com.warrencrasta.fantasy.yahoo.service.core.user.UserService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(MockitoExtension.class)
class SeasonControllerTest {
  private MockMvc mockMvc;
  @Mock private UserService userService;

  @BeforeEach
  void setUp() {
    this.mockMvc = standaloneSetup(new SeasonController(userService)).build();
  }

  @Test
  void testGetLeagues() throws Exception {
    String seasonId = "395";
    List<? extends League> leagues =
        Collections.singletonList(new YahooLeague("395.l.37133", "Father Stretch the Floor"));

    doReturn(leagues).when(userService).getLeaguesForUser(seasonId);

    String uri = String.format("/seasons/%s/leagues", seasonId);
    mockMvc
        .perform(get(uri))
        .andExpect(status().isOk())
        .andExpect(
            content().json("[{\"id\":\"395.l.37133\",\"name\":\"Father Stretch the Floor\"}]"));
  }
}
