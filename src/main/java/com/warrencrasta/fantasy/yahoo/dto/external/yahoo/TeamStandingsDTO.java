package com.warrencrasta.fantasy.yahoo.dto.external.yahoo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeamStandingsDTO {

  private Integer rank;

  @JsonAlias("outcome_totals")
  private OutcomeTotalsDTO outcomeTotals;
}