package com.warrencrasta.fantasy.yahoo.domain.team;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YahooTeam extends Team {
  private String teamKey;
  private String name;
  private String logoUrl;

  private Double strengthOfSchedule;
}
