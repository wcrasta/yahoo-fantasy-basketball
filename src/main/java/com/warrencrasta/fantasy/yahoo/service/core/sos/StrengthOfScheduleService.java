package com.warrencrasta.fantasy.yahoo.service.core.sos;

import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.LeagueDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.YahooMatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import com.warrencrasta.fantasy.yahoo.service.core.stat.StatService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class StrengthOfScheduleService {

  private final LeagueService leagueService;
  private final StatService statService;

  public StrengthOfScheduleService(LeagueService leagueService, StatService statService) {
    this.leagueService = leagueService;
    this.statService = statService;
  }

  public Map<String, Double> calculateStrengthOfScheduleForLeague(
      String leagueId, LeagueDTO leagueInfo) {
      
    Map<String, Double> teamWinRates = leagueService.getLeagueWinRates(leagueId);

    int currentWeek = Integer.parseInt(leagueInfo.getCurrentWeek());
    Map<String, Double> strengthOfScheduleMap = new HashMap<>();

    for (String teamKey : teamWinRates.keySet()) {
      List<YahooMatchupDTO> seasonMatchups = statService.getSeasonMatchupsForTeam(teamKey);

      List<YahooMatchupDTO> remainingMatchups = seasonMatchups.stream()
          .filter(matchup -> Integer.parseInt(matchup.getWeek()) > currentWeek)
          .collect(Collectors.toList());

      if (remainingMatchups.isEmpty()) {
        strengthOfScheduleMap.put(teamKey, 0.0);
        continue;
      }

      double totalOpponentWinRate = 0.0;
      for (YahooMatchupDTO matchup : remainingMatchups) {
        String opponentKey = matchup.getTeams().stream()
            .map(teamWrapper -> teamWrapper.getTeam().getTeamKey())
            .filter(key -> !key.equals(teamKey))
            .findFirst()
            .orElse(null);

        if (opponentKey != null) {
          double opponentWinRate = teamWinRates.getOrDefault(opponentKey, 0.500);
          totalOpponentWinRate += opponentWinRate;
        }
      }
        
      double averageOpponentWinRate = totalOpponentWinRate / remainingMatchups.size();
      strengthOfScheduleMap.put(teamKey, averageOpponentWinRate);
    }
    return strengthOfScheduleMap;
  }
}