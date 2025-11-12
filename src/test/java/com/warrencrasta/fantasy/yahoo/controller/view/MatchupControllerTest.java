package com.warrencrasta.fantasy.yahoo.controller.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import com.warrencrasta.fantasy.yahoo.service.core.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@ExtendWith(MockitoExtension.class)
class MatchupControllerTest {

  private MockMvc mockMvc;
  @Mock private UserService userService;

  @BeforeEach
  void setUp() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/templates");
    viewResolver.setSuffix(".html");
    this.mockMvc =
        standaloneSetup(new MatchupController(userService)).setViewResolvers(viewResolver).build();
  }

  @Test
  void testWeeklyMatchups() throws Exception {
    mockMvc
        .perform(get("/matchup-comparisons"))
        .andExpect(status().isOk())
        .andExpect(model().attributeExists("seasons"))
        .andExpect(view().name("matchup-comparisons"));
  }
}
