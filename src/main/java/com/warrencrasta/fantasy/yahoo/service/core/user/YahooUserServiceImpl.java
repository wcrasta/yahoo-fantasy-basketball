package com.warrencrasta.fantasy.yahoo.service.core.user;

import com.warrencrasta.fantasy.yahoo.domain.league.YahooLeague;
import com.warrencrasta.fantasy.yahoo.domain.season.YahooSeason;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyContentDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.GameWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.LeagueWrapperDTO;
import com.warrencrasta.fantasy.yahoo.mapper.LeagueMapper;
import com.warrencrasta.fantasy.yahoo.mapper.SeasonMapper;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class YahooUserServiceImpl implements UserService {

  private final YahooClient yahooClient;
  private final SeasonMapper seasonMapper;
  private final LeagueMapper leagueMapper;

  public YahooUserServiceImpl(
      YahooClient yahooClient, SeasonMapper seasonMapper, LeagueMapper leagueMapper) {
    this.yahooClient = yahooClient;
    this.seasonMapper = seasonMapper;
    this.leagueMapper = leagueMapper;
  }

  @Override
  public List<YahooSeason> getSeasonsForUser() {
    var resourceUriFragment = "/users;use_login=1/games;game_codes=nba";

    FantasyContentDTO fantasyContent = yahooClient.getFantasyContent(resourceUriFragment);

    List<GameWrapperDTO> gameWrapperDTOs = fantasyContent.getUsers().get(0).getUser().getGames();

    List<YahooSeason> seasons = seasonMapper.gameWrapperDTOsToYahooSeasons(gameWrapperDTOs);
    // We want more recent seasons to be first
    Collections.reverse(seasons);

    return seasons;
  }

  @Override
  public List<YahooLeague> getLeaguesForUser(String seasonId) {
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("game_key", seasonId);
    var resourceUriFragment = "/users;use_login=1/games;game_keys={game_key}/leagues";

    FantasyContentDTO fantasyContent =
        yahooClient.getFantasyContent(uriVariables, resourceUriFragment);

    List<LeagueWrapperDTO> leagueWrapperDTOs =
        fantasyContent.getUsers().get(0).getUser().getGames().get(0).getGame().getLeagues();

    List<YahooLeague> yahooLeagues =
        leagueMapper.leagueWrapperDTOsToYahooLeagues(leagueWrapperDTOs);
    yahooLeagues.sort(Comparator.comparing(YahooLeague::getName));

    return yahooLeagues;
  }
}
