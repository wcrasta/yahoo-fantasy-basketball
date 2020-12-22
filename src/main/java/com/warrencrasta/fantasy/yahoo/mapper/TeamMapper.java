package com.warrencrasta.fantasy.yahoo.mapper;

import com.warrencrasta.fantasy.yahoo.domain.team.YahooTeam;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.TeamWrapperDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TeamMapper {

  List<YahooTeam> teamWrapperDTOsToYahooTeams(List<TeamWrapperDTO> teamWrapperDTOs);

  @Mapping(source = "team.teamKey", target = "id")
  @Mapping(source = "team.name", target = "name")
  YahooTeam teamWrapperDTOtoYahooTeam(TeamWrapperDTO teamWrapperDTO);
}
