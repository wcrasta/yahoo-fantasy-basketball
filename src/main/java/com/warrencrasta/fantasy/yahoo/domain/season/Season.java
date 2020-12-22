package com.warrencrasta.fantasy.yahoo.domain.season;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Season {

  private String id;
  private String year;
}
