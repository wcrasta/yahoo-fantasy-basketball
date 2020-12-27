package com.warrencrasta.fantasy.yahoo.service.core.league;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.team.YahooTeam;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyContentDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.LeagueDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.StatDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.StatWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.TeamWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.internal.LeagueInfoDTO;
import com.warrencrasta.fantasy.yahoo.mapper.TeamMapper;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class YahooLeagueServiceImpl implements LeagueService {

  private final YahooClient yahooClient;
  private final TeamMapper teamMapper;

  public YahooLeagueServiceImpl(YahooClient yahooClient, TeamMapper teamMapper) {
    this.yahooClient = yahooClient;
    this.teamMapper = teamMapper;
  }

  @Override
  public LeagueInfoDTO getLeagueInfo(String leagueId) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    String resourceUriFragment = "/league/{league_key}/teams";

    FantasyContentDTO fantasyContent =
        yahooClient.getFantasyContent(uriVariables, resourceUriFragment);
    LeagueDTO leagueDTO = fantasyContent.getLeague();
    List<YahooTeam> teams = extractTeams(leagueDTO);
    List<String> weeks = extractWeeks(leagueDTO);

    LeagueInfoDTO leagueInfoDTO = new LeagueInfoDTO();
    leagueInfoDTO.setTeams(teams);
    leagueInfoDTO.setWeeks(weeks);

    return leagueInfoDTO;
  }

  @Override
  public List<StatCategory> getRelevantCategories(String leagueId) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    String resourceUriFragment = "/league/{league_key}/settings";

    FantasyContentDTO fantasyContent =
        yahooClient.getFantasyContent(uriVariables, resourceUriFragment);
    List<StatWrapperDTO> statWrapperDTOs =
        fantasyContent.getLeague().getSettings().getStatCategories().getStats();

    List<StatCategory> relevantCategories = new ArrayList<>();
    for (StatWrapperDTO statWrapperDTO : statWrapperDTOs) {
      StatDTO statDTO = statWrapperDTO.getStat();
      if (statDTO.getIsOnlyDisplayStat() != null) {
        continue;
      }

      StatCategory statCategory = new StatCategory();
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

    int startWeek = Integer.parseInt(leagueDTO.getStartWeek());
    int currentWeek = Integer.parseInt(leagueDTO.getCurrentWeek());

    for (int i = currentWeek; i >= startWeek; i--) {
      weeks.add("Week " + i);
    }

    return weeks;
  }
}
