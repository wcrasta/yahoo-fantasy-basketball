package com.warrencrasta.fantasy.yahoo.controller.view;

import com.warrencrasta.fantasy.yahoo.domain.season.YahooSeason;
import com.warrencrasta.fantasy.yahoo.service.core.user.UserService;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchupController {

  private final UserService userService;

  public MatchupController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/matchup-comparisons")
  public String weeklyMatchups(Model model) {
    model.addAttribute("seasons", userService.getSeasonsForUser());
    return "matchup-comparisons";
  }

  @GetMapping("/strength-of-schedule")
  public String strengthOfSchedule(Model model) {
    model.addAttribute("seasons", userService.getSeasonsForUser());
    return "strength-of-schedule";
  }

  @GetMapping("/power-rankings")
  public String powerRankings(Model model) {
    var seasons = userService.getSeasonsForUser();
    model.addAttribute("seasons", seasons);
    return "power-rankings";
  }

  @GetMapping("/live-standings")
  public String liveStandings(Model model) {
    var seasons = userService.getSeasonsForUser();
    model.addAttribute("seasons", seasons);
    return "live-standings";
  }
}