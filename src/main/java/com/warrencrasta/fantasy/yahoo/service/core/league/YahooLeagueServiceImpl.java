package com.warrencrasta.fantasy.yahoo.service.core.league;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.team.YahooTeam;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyContentDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.LeagueDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.StatWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.TeamWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.internal.LeagueInfoDTO;
import com.warrencrasta.fantasy.yahoo.mapper.TeamMapper;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import com.warrencrasta.fantasy.yahoo.service.core.sos.StrengthOfScheduleService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class YahooLeagueServiceImpl implements LeagueService {

  private final YahooClient yahooClient;
  private final TeamMapper teamMapper;
  private final StrengthOfScheduleService strengthOfScheduleService;

  public YahooLeagueServiceImpl(YahooClient yahooClient, TeamMapper teamMapper, 
      @Lazy StrengthOfScheduleService strengthOfScheduleService) {
    this.yahooClient = yahooClient;
    this.teamMapper = teamMapper;
    this.strengthOfScheduleService = strengthOfScheduleService;
  }

  @Override
  public LeagueInfoDTO getLeagueInfo(String leagueId) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    var resourceUriFragment = "/league/{league_key}/teams";

    FantasyContentDTO fantasyContent =
        yahooClient.getFantasyContent(uriVariables, resourceUriFragment);
    var leagueDTO = fantasyContent.getLeague();
    List<YahooTeam> teams = extractTeams(leagueDTO);
    teams.sort(Comparator.comparing(YahooTeam::getName));
    List<String> weeks = extractWeeks(leagueDTO);

    var leagueInfoDTO = new LeagueInfoDTO();
    leagueInfoDTO.setTeams(teams);
    leagueInfoDTO.setWeeks(weeks);

    return leagueInfoDTO;
  }

  @Override
  public LeagueInfoDTO getLeagueInfoWithSos(String leagueId) {
    LeagueInfoDTO leagueInfoDTO = this.getLeagueInfo(leagueId);
    
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    FantasyContentDTO fantasyContent = 
        yahooClient.getFantasyContent(uriVariables, "/league/{league_key}/teams");
    var leagueDTO = fantasyContent.getLeague();

    try {
      Map<String, Double> sosMap =
          strengthOfScheduleService.calculateStrengthOfScheduleForLeague(leagueId, leagueDTO);

      for (YahooTeam team : leagueInfoDTO.getTeams()) {
        Double sosScore = sosMap.get(team.getId());
        if (sosScore != null) {
          team.setStrengthOfSchedule(sosScore);
        } else {
          team.setStrengthOfSchedule(0.0);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return leagueInfoDTO;
  }

  @Override
  public List<StatCategory> getRelevantCategories(String leagueId) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    var resourceUriFragment = "/league/{league_key}/settings";

    FantasyContentDTO fantasyContent =
        yahooClient.getFantasyContent(uriVariables, resourceUriFragment);
    List<StatWrapperDTO> statWrapperDTOs =
        fantasyContent.getLeague().getSettings().getStatCategories().getStats();

    List<StatCategory> relevantCategories = new ArrayList<>();
    for (StatWrapperDTO statWrapperDTO : statWrapperDTOs) {
      var statDTO = statWrapperDTO.getStat();
      if (statDTO.getIsOnlyDisplayStat() != null) {
        continue;
      }

      var statCategory = new StatCategory();
      statCategory.setId(statDTO.getStatId());
      statCategory.setName(statDTO.getDisplayName());
      statCategory.setBad(statDTO.getSortOrder().equals("0"));

      relevantCategories.add(statCategory);
    }

    return relevantCategories;
  }

  private List<YahooTeam> extractTeams(LeagueDTO leagueDTO) {
    List<TeamWrapperDTO> teamWrappers = leagueDTO.getTeams();
    return teamMapper.teamWrapperDTOsToYahooTeams(teamWrappers);
  }

  private List<String> extractWeeks(LeagueDTO leagueDTO) {
    List<String> weeks = new ArrayList<>();

    var startWeek = Integer.parseInt(leagueDTO.getStartWeek());
    var currentWeek = Integer.parseInt(leagueDTO.getCurrentWeek());

    for (int i = currentWeek; i >= startWeek; i--) {
      weeks.add("Week " + i);
    }

    return weeks;
  }

  @Override
  public Map<String, Double> getLeagueWinRates(String leagueId) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    var resourceUriFragment = "/league/{league_key}/standings";

    FantasyContentDTO fantasyContent =
        yahooClient.getFantasyContent(uriVariables, resourceUriFragment);

    Map<String, Double> teamWinRates = new HashMap<>();

    List<TeamWrapperDTO> teamWrappers =
        fantasyContent.getLeague().getStandings().getTeams();

    for (TeamWrapperDTO teamWrapper : teamWrappers) {
      var teamDTO = teamWrapper.getTeam();
      String teamKey = teamDTO.getTeamKey();
      
      var outcomes = teamDTO.getTeamStandings().getOutcomeTotals();
      
      double wins = Double.parseDouble(outcomes.getWins());
      double losses = Double.parseDouble(outcomes.getLosses());
      double ties = Double.parseDouble(outcomes.getTies());
      
      double totalGames = wins + losses + ties;
      
      double winRate = 0.0;
      if (totalGames > 0) {
        winRate = (wins + (ties * 0.5)) / totalGames;
      }

      teamWinRates.put(teamKey, winRate);
    }

    return teamWinRates;
  }
}
