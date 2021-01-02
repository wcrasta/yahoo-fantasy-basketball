package com.warrencrasta.fantasy.yahoo.dto.external.yahoo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatDTO {

  @JsonAlias({"stat_id"})
  private String statId;

  @JsonAlias({"display_name"})
  private String displayName;

  @JsonAlias({"sort_order"})
  private String sortOrder;

  @JsonAlias({"is_only_display_stat"})
  private String isOnlyDisplayStat;

  private String value;
}
