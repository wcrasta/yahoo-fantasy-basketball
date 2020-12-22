package com.warrencrasta.fantasy.yahoo.service.core.scoreboard;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.stat.TeamStatCategory;
import com.warrencrasta.fantasy.yahoo.dto.internal.MatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import com.warrencrasta.fantasy.yahoo.service.core.team.TeamService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class YahooScoreboardServiceImpl implements ScoreboardService {

  private final LeagueService leagueService;
  private final TeamService teamService;

  public YahooScoreboardServiceImpl(LeagueService leagueService,
      TeamService teamService) {
    this.leagueService = leagueService;
    this.teamService = teamService;
  }

  @Override
  public List<MatchupDTO> getWeeklyMatchups(String leagueId, String week, String teamId) {
    List<StatCategory> relevantCategories = leagueService.getRelevantCategories(leagueId);
    List<TeamStatCategory> teamStatCategories = teamService
        .getAllTeamsStats(leagueId, week, relevantCategories);

    TeamStatCategory myStats = null;
    for (TeamStatCategory team : teamStatCategories) {
      if (team.getId().equals(teamId)) {
        myStats = team;
        teamStatCategories.remove(team);
        break;
      }
    }

    List<MatchupDTO> matchups = new ArrayList<>();
    for (TeamStatCategory opponent : teamStatCategories) {
      MatchupDTO matchup = new MatchupDTO();
      matchup.setOpponent(opponent.getName());

      List<String> categoriesWon = new ArrayList<>();
      List<String> categoriesLost = new ArrayList<>();
      List<String> categoriesTied = new ArrayList<>();

      List<StatCategory> myStatCategories = myStats.getStatCategories();
      for (int i = 0; i < myStatCategories.size(); i++) {
        double myValue = 0.0;
        double opponentValue = 0.0;

        if (myStatCategories.get(i).getValue() != null) {
          myValue = Double.parseDouble(myStatCategories.get(i).getValue());
        }

        if (opponent.getStatCategories().get(i).getValue() != null) {
          opponentValue = Double.parseDouble(opponent.getStatCategories().get(i).getValue());
        }

        String categoryName = myStatCategories.get(i).getName();
        boolean isBad = myStatCategories.get(i).isBad();
        int comparison = Double.compare(myValue, opponentValue);

        if (comparison > 0) {
          if (isBad) {
            categoriesLost.add(categoryName);
          } else {
            categoriesWon.add(categoryName);
          }
        } else if (comparison < 0) {
          if (isBad) {
            categoriesWon.add(categoryName);
          } else {
            categoriesLost.add(categoryName);
          }
        } else {
          categoriesTied.add(categoryName);
        }
      }

      matchup.setCategoriesWon(categoriesWon);
      matchup.setCategoriesLost(categoriesLost);
      matchup.setCategoriesTied(categoriesTied);

      matchups.add(matchup);
    }

    return matchups;
  }

}
