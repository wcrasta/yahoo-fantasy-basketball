package com.warrencrasta.fantasy.yahoo.dto.external.yahoo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.Data;

/**
 * Used by Jackson for deserialization of Yahoo Fantasy API JSON response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreboardDTO {

  private List<MatchupWrapperDTO> matchups;
}
