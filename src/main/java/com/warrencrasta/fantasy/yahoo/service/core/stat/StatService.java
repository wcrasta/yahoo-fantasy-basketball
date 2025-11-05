package com.warrencrasta.fantasy.yahoo.service.core.stat;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.stat.TeamStatCategory;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.YahooMatchupDTO;
import java.util.List;

public interface StatService {

  List<TeamStatCategory> getAllTeamsStats(
      String leagueId, String week, List<StatCategory> relevantCategories);
      
  List<YahooMatchupDTO> getSeasonMatchupsForTeam(String teamKey);
}
