package com.warrencrasta.fantasy.yahoo.domain.league;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class League {

  private String id;
  private String name;
}
