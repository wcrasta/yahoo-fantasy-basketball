package com.warrencrasta.fantasy.yahoo.mapper;

import com.warrencrasta.fantasy.yahoo.domain.season.YahooSeason;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.GameWrapperDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeasonMapper {

  List<YahooSeason> gameWrapperDTOsToYahooSeasons(List<GameWrapperDTO> gameWrapperDTOs);

  @Mapping(source = "game.gameId", target = "id")
  @Mapping(source = "game.season", target = "year")
  YahooSeason gameWrapperDTOtoYahooSeason(GameWrapperDTO gameWrapperDTO);

}