package com.warrencrasta.fantasy.yahoo.dto.internal;

import com.warrencrasta.fantasy.yahoo.domain.team.YahooTeam;
import java.util.List;
import lombok.Data;

@Data
public class LeagueInfoDTO {

  private List<String> weeks;
  private List<YahooTeam> teams;
}
