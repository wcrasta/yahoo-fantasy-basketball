package com.warrencrasta.fantasy.yahoo.dto.external.yahoo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YahooMatchupDTO {

  @JsonAlias("stat_winners")
  private List<StatWinnerWrapperDTO> statWinners;

  private List<TeamWrapperDTO> teams;
}
