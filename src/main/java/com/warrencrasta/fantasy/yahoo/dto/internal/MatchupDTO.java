package com.warrencrasta.fantasy.yahoo.dto.internal;

import java.util.List;
import lombok.Data;


@Data
public class MatchupDTO {

  private String opponent;
  private List<String> categoriesWon;
  private List<String> categoriesLost;
  private List<String> categoriesTied;
}
