package com.warrencrasta.fantasy.yahoo.dto.external.yahoo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamDTO {

  @JsonAlias({"team_key"})
  private String teamKey;

  private String name;

  @JsonAlias({"team_stats"})
  private TeamStatsDTO teamStats;

  @JsonAlias({"team_standings"})
  private TeamStandingsDTO teamStandings;

  private List<MatchupWrapperDTO> matchups;
}