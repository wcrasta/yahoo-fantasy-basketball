package com.warrencrasta.fantasy.yahoo.service.core.team;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.stat.TeamStatCategory;
import java.util.List;

public interface TeamService {

  List<TeamStatCategory> getAllTeamsStats(String leagueId, String week,
      List<StatCategory> relevantCategories);
}
