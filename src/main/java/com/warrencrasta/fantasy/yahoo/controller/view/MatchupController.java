package com.warrencrasta.fantasy.yahoo.controller.view;

import com.warrencrasta.fantasy.yahoo.service.core.user.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchupController {

  private final UserService userService;

  public MatchupController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/weekly-matchups")
  public String weeklyMatchups(Model model) {
    model.addAttribute("seasons", userService.getSeasonsForUser());
    return "weekly-matchups";
  }
}