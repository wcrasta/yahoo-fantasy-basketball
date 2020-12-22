package com.warrencrasta.fantasy.yahoo.service.core.user;

import com.warrencrasta.fantasy.yahoo.domain.league.League;
import com.warrencrasta.fantasy.yahoo.domain.season.YahooSeason;
import java.util.List;

public interface UserService {

  List<? extends YahooSeason> getSeasonsForUser();

  List<? extends League> getLeaguesForUser(String seasonId);
}
