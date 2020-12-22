package com.warrencrasta.fantasy.yahoo.dto.external.yahoo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Used by Jackson for deserialization of Yahoo Fantasy API JSON response.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameWrapperDTO {

  private GameDTO game;
}
