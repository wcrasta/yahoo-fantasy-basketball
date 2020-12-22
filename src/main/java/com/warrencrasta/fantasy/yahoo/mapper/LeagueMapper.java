package com.warrencrasta.fantasy.yahoo.mapper;

import com.warrencrasta.fantasy.yahoo.domain.league.YahooLeague;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.LeagueWrapperDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LeagueMapper {

  List<YahooLeague> leagueWrapperDTOsToYahooLeagues(List<LeagueWrapperDTO> leagueWrapperDTOs);

  @Mapping(source = "league.name", target = "name")
  @Mapping(source = "league.leagueKey", target = "id")
  YahooLeague leagueWrapperDTOtoYahooLeague(LeagueWrapperDTO leagueWrapperDTO);

}
