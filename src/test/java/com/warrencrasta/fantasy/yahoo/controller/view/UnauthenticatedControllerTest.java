package com.warrencrasta.fantasy.yahoo.controller.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

class UnauthenticatedControllerTest {
  private MockMvc mockMvc;

  @BeforeEach
  void setup() {
    InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
    viewResolver.setPrefix("/templates");
    viewResolver.setSuffix(".html");
    this.mockMvc =
        standaloneSetup(new UnauthenticatedController()).setViewResolvers(viewResolver).build();
  }

  @Test
  void testRedirectToSignIn() throws Exception {
    mockMvc
        .perform(get("/"))
        .andExpect(redirectedUrl("/signin"))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void testGetSignInPage() throws Exception {
    mockMvc.perform(get("/signin")).andExpect(status().isOk()).andExpect(view().name("signin"));
  }

  @Test
  void testGetExamplesPage() throws Exception {
    mockMvc.perform(get("/examples")).andExpect(status().isOk()).andExpect(view().name("examples"));
  }

  @Test
  void testGetContactPage() throws Exception {
    mockMvc.perform(get("/contact")).andExpect(status().isOk()).andExpect(view().name("contact"));
  }
}
