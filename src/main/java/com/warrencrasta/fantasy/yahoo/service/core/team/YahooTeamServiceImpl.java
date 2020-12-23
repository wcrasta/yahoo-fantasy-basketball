package com.warrencrasta.fantasy.yahoo.service.core.team;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.stat.TeamStatCategory;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyContentDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.MatchupWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.StatDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.StatWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.TeamDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.TeamWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.YahooMatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class YahooTeamServiceImpl implements TeamService {

  private final YahooClient yahooClient;

  public YahooTeamServiceImpl(YahooClient yahooClient) {
    this.yahooClient = yahooClient;
  }

  @Override
  public List<TeamStatCategory> getAllTeamsStats(String leagueId, String week,
      List<StatCategory> relevantCategories) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    uriVariables.put("week", week);
    String path = "/league/{league_key}/scoreboard;week={week}";

    FantasyContentDTO fantasyContent = yahooClient.getFantasyContent(uriVariables, path);

    List<MatchupWrapperDTO> matchupWrapperDTOs = fantasyContent
        .getLeague()
        .getScoreboard()
        .getMatchups();

    List<TeamStatCategory> allTeamsStats = new ArrayList<>();

    for (MatchupWrapperDTO matchupWrapperDTO : matchupWrapperDTOs) {
      YahooMatchupDTO matchupDTO = matchupWrapperDTO.getMatchup();
      List<TeamWrapperDTO> matchupTeams = matchupDTO.getTeams();
      for (TeamWrapperDTO matchupTeam : matchupTeams) {
        TeamDTO matchupTeamDTO = matchupTeam.getTeam();
        TeamStatCategory statsForTeam = new TeamStatCategory();
        statsForTeam.setId(matchupTeamDTO.getTeamKey());
        statsForTeam.setName(matchupTeamDTO.getName());
        List<StatCategory> statCategories = new ArrayList<>();
        List<StatWrapperDTO> statDTOs = matchupTeamDTO.getTeamStats().getStats();

        for (StatWrapperDTO statWrapperDTO : statDTOs) {
          StatDTO statDTO = statWrapperDTO.getStat();
          if (statDTO.getValue() != null && statDTO.getValue().equals("-")) {
            statDTO.setValue("0");
          }

          for (StatCategory relevantCategory : relevantCategories) {
            if (relevantCategory.getId().equals(statDTO.getStatId())) {
              StatCategory statCategory = new StatCategory();
              statCategory.setId(statDTO.getStatId());
              statCategory.setName(relevantCategory.getName());
              statCategory.setValue(statDTO.getValue());
              statCategory.setBad(relevantCategory.isBad());
              statCategories.add(statCategory);
            }
          }
        }

        statsForTeam.setStatCategories(statCategories);
        allTeamsStats.add(statsForTeam);
      }
    }

    return allTeamsStats;
  }
}
