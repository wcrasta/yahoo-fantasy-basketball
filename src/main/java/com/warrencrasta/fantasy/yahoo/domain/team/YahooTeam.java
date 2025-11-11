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

  private transient double totalCategoriesWon = 0.0;
  private transient double totalCategoriesTied = 0.0;
  private transient int totalCategoriesPlayed = 0;
  private transient double winRate = 0.0;
  
  public void addWeeklyWins(double wins) {
    this.totalCategoriesWon += wins;
  }
  
  public void addWeeklyTies(double ties) {
    this.totalCategoriesTied += ties;
  }

  public void addWeeklyCategoriesPlayed(int categories) {
    this.totalCategoriesPlayed += categories;
  }

  public void calculateFinalWinRate() {
    if (totalCategoriesPlayed > 0) {
      this.winRate = (this.totalCategoriesWon 
          + (this.totalCategoriesTied * 0.5)) / this.totalCategoriesPlayed;
    }
  }

}
