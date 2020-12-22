package com.warrencrasta.fantasy.yahoo.service.core.league;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.dto.internal.LeagueInfoDTO;
import java.util.List;

public interface LeagueService {

  LeagueInfoDTO getLeagueInfo(String leagueId);

  List<StatCategory> getRelevantCategories(String leagueId);
}
