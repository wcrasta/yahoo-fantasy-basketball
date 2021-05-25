package com.warrencrasta.fantasy.yahoo.service.core.scoreboard;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.stat.TeamStatCategory;
import com.warrencrasta.fantasy.yahoo.dto.internal.MatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import com.warrencrasta.fantasy.yahoo.service.core.stat.StatService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;

@Service
public class YahooScoreboardServiceImpl implements ScoreboardService {

  private final LeagueService leagueService;
  private final StatService statService;

  public YahooScoreboardServiceImpl(LeagueService leagueService, StatService statService) {
    this.leagueService = leagueService;
    this.statService = statService;
  }

  @Override
  public List<MatchupDTO> getWeeklyMatchups(String leagueId, String week, String teamId) {
    List<StatCategory> relevantCategories = leagueService.getRelevantCategories(leagueId);
    List<TeamStatCategory> allTeamsStats =
        statService.getAllTeamsStats(leagueId, week, relevantCategories);
    TeamStatCategory myStats = Objects.requireNonNull(extractMyStats(teamId, allTeamsStats));
    return compareAgainstOpponents(myStats, allTeamsStats);
  }

  private TeamStatCategory extractMyStats(String teamId, List<TeamStatCategory> allTeamsStats) {
    TeamStatCategory myStats = null;
    for (TeamStatCategory team : allTeamsStats) {
      if (team.getId().equals(teamId)) {
        myStats = team;
        allTeamsStats.remove(team);
        break;
      }
    }
    return myStats;
  }

  private List<MatchupDTO> compareAgainstOpponents(
      TeamStatCategory myStats, List<TeamStatCategory> allOpponentsStats) {
    List<MatchupDTO> weeklyMatchupsList = new ArrayList<>();
    for (TeamStatCategory opponentStats : allOpponentsStats) {
      var matchup = new MatchupDTO();
      matchup.setOpponent(opponentStats.getName());

      List<String> categoriesWon = new ArrayList<>();
      List<String> categoriesLost = new ArrayList<>();
      List<String> categoriesTied = new ArrayList<>();

      calculateScore(
          myStats.getStatCategories(),
          opponentStats,
          categoriesWon,
          categoriesLost,
          categoriesTied);

      matchup.setCategoriesWon(categoriesWon);
      matchup.setCategoriesLost(categoriesLost);
      matchup.setCategoriesTied(categoriesTied);

      weeklyMatchupsList.add(matchup);
    }
    return weeklyMatchupsList;
  }

  private void calculateScore(
      List<StatCategory> myStatCategories,
      TeamStatCategory opponent,
      List<String> categoriesWon,
      List<String> categoriesLost,
      List<String> categoriesTied) {

    for (var i = 0; i < myStatCategories.size(); i++) {
      var myValue = Double.parseDouble(myStatCategories.get(i).getValue());
      var opponentValue = Double.parseDouble(opponent.getStatCategories().get(i).getValue());

      String categoryName = myStatCategories.get(i).getName();
      boolean isBad = myStatCategories.get(i).isBad();
      int comparison = Double.compare(myValue, opponentValue);

      if (comparison == 0) {
        categoriesTied.add(categoryName);
      } else if ((comparison > 0 && !isBad) || (comparison < 0 && isBad)) {
        categoriesWon.add(categoryName);
      } else {
        categoriesLost.add(categoryName);
      }
    }
  }
}
