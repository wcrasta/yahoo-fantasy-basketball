package com.warrencrasta.fantasy.yahoo.domain.stat;

import com.warrencrasta.fantasy.yahoo.domain.team.Team;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamStatCategory extends Team {

  private List<StatCategory> statCategories;
}
