package com.warrencrasta.fantasy.yahoo.controller.rest;

import com.warrencrasta.fantasy.yahoo.domain.team.YahooTeam;
import com.warrencrasta.fantasy.yahoo.dto.internal.LeagueInfoDTO;
import com.warrencrasta.fantasy.yahoo.dto.internal.MatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import com.warrencrasta.fantasy.yahoo.service.core.livestandings.LiveStandingsService;
import com.warrencrasta.fantasy.yahoo.service.core.powerranking.PowerRankingService;
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

  private final LiveStandingsService liveStandingsService;
  private final LeagueService leagueService;
  private final ScoreboardService scoreboardService;
  private final PowerRankingService powerRankingService;

  public LeagueController(LeagueService leagueService, ScoreboardService scoreboardService, 
      PowerRankingService powerRankingService, LiveStandingsService liveStandingsService) {
    this.leagueService = leagueService;
    this.liveStandingsService = liveStandingsService;
    this.scoreboardService = scoreboardService;
    this.powerRankingService = powerRankingService;
  }

  @GetMapping("/{leagueId}/info")
  public LeagueInfoDTO getLeagueInfo(@PathVariable @NotBlank String leagueId) {
    return leagueService.getLeagueInfo(leagueId);
  }

  @GetMapping("/{leagueId}/sos-info")
  public LeagueInfoDTO getLeagueInfoWithSos(@PathVariable @NotBlank String leagueId) {
    return leagueService.getLeagueInfoWithSos(leagueId);
  }

  @GetMapping("/{leagueId}/power-rankings")
  public List<YahooTeam> getPowerRankings(@PathVariable String leagueId) {
    return powerRankingService.calculatePowerRankings(leagueId);
  }

  @GetMapping("/{leagueId}/weekly-matchups")
  public List<MatchupDTO> getWeeklyMatchups(
      @PathVariable String leagueId, @RequestParam String week, @RequestParam String teamId) {
    return scoreboardService.getWeeklyMatchups(leagueId, week, teamId);
  }

  @GetMapping("/{leagueId}/live-standings")
  public List<YahooTeam> getLiveStandings(@PathVariable String leagueId) {
    // Şimdilik direkt servisi çağıralım (servisi constructor'a eklemeyi unutmayın!)
    return liveStandingsService.getLiveStandings(leagueId);
  }
}
