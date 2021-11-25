package com.warrencrasta.fantasy.yahoo.controller.rest;

import com.warrencrasta.fantasy.yahoo.dto.internal.LeagueInfoDTO;
import com.warrencrasta.fantasy.yahoo.dto.internal.MatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import com.warrencrasta.fantasy.yahoo.service.core.scoreboard.ScoreboardService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leagues")
@Validated
public class LeagueController {

  private final LeagueService leagueService;
  private final ScoreboardService scoreboardService;

  public LeagueController(LeagueService leagueService, ScoreboardService scoreboardService) {
    this.leagueService = leagueService;
    this.scoreboardService = scoreboardService;
  }

  @GetMapping("/{leagueId}/info")
  public LeagueInfoDTO getLeagueInfo(@PathVariable @NotBlank String leagueId) {
    return leagueService.getLeagueInfo(leagueId);
  }

  @GetMapping("/{leagueId}/weekly-matchups")
  public List<MatchupDTO> getWeeklyMatchups(
      @PathVariable String leagueId, @RequestParam String week, @RequestParam String teamId) {
    return scoreboardService.getWeeklyMatchups(leagueId, week, teamId);
  }
}
