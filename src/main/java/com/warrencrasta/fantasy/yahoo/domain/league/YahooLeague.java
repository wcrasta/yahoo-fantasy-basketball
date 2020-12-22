package com.warrencrasta.fantasy.yahoo.domain.league;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YahooLeague extends League {
  public YahooLeague(String id, String name) {
    super(id, name);
  }
}
