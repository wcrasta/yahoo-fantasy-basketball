package com.warrencrasta.fantasy.yahoo.dto.external.yahoo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

/**
 * Used by Jackson for deserialization of Yahoo Fantasy API JSON response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeagueDTO {

  private String name;

  @JsonAlias({"league_key"})
  private String leagueKey;

  @JsonAlias({"current_week"})
  private String currentWeek;

  @JsonAlias({"start_week"})
  private String startWeek;

  private List<TeamWrapperDTO> teams;

  private ScoreboardDTO scoreboard;

  private SettingsDTO settings;
}
