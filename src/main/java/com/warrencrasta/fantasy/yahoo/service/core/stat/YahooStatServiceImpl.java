package com.warrencrasta.fantasy.yahoo.service.core.stat;

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
public class YahooStatServiceImpl implements StatService {

  private final YahooClient yahooClient;

  public YahooStatServiceImpl(YahooClient yahooClient) {
    this.yahooClient = yahooClient;
  }

  @Override
  public List<TeamStatCategory> getAllTeamsStats(
      String leagueId, String week, List<StatCategory> relevantCategories) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    uriVariables.put("week", week);
    String resourceUriFragment = "/league/{league_key}/scoreboard;week={week}";

    FantasyContentDTO fantasyContent =
        yahooClient.getFantasyContent(uriVariables, resourceUriFragment);

    List<MatchupWrapperDTO> matchupWrapperDTOs =
        fantasyContent.getLeague().getScoreboard().getMatchups();

    return populateAllTeamsStats(relevantCategories, matchupWrapperDTOs);
  }

  private List<TeamStatCategory> populateAllTeamsStats(
      List<StatCategory> relevantCategories, List<MatchupWrapperDTO> matchupWrapperDTOs) {
    List<TeamStatCategory> allTeamsStats = new ArrayList<>();

    for (MatchupWrapperDTO matchupWrapperDTO : matchupWrapperDTOs) {
      YahooMatchupDTO matchupDTO = matchupWrapperDTO.getMatchup();
      List<TeamWrapperDTO> teamWrapperDTOs = matchupDTO.getTeams();
      for (TeamWrapperDTO teamWrapperDTO : teamWrapperDTOs) {
        TeamDTO teamDTO = teamWrapperDTO.getTeam();
        TeamStatCategory singleTeamStats = populateSingleTeamStats(relevantCategories, teamDTO);
        allTeamsStats.add(singleTeamStats);
      }
    }

    return allTeamsStats;
  }

  private TeamStatCategory populateSingleTeamStats(
      List<StatCategory> relevantCategories, TeamDTO teamDTO) {
    TeamStatCategory statsForTeam = new TeamStatCategory();
    statsForTeam.setId(teamDTO.getTeamKey());
    statsForTeam.setName(teamDTO.getName());

    List<StatCategory> statCategories = new ArrayList<>();
    List<StatWrapperDTO> statDTOs = teamDTO.getTeamStats().getStats();

    for (StatWrapperDTO statWrapperDTO : statDTOs) {
      StatDTO statDTO = statWrapperDTO.getStat();
      if (statDTO.getValue() == null || !isNumeric(statDTO.getValue())) {
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
          break;
        }
      }
    }

    statsForTeam.setStatCategories(statCategories);
    return statsForTeam;
  }

  // https://stackoverflow.com/questions/1102891/how-to-check-if-a-string-is-numeric-in-java
  private static boolean isNumeric(String str) {
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
