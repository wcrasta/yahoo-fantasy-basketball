package com.warrencrasta.fantasy.yahoo.domain.season;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class YahooSeason extends Season {
  public YahooSeason(String id, String year) {
    super(id, year);
  }
}
