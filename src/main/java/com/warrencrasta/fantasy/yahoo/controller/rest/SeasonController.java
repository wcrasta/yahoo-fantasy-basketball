package com.warrencrasta.fantasy.yahoo.controller.rest;

import com.warrencrasta.fantasy.yahoo.domain.league.League;
import com.warrencrasta.fantasy.yahoo.service.core.user.UserService;
import java.util.List;
import javax.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seasons")
@Validated
public class SeasonController {

  private final UserService userService;

  public SeasonController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/{seasonId}/leagues")
  public List<? extends League> getLeagues(@PathVariable @NotBlank String seasonId) {
    return userService.getLeaguesForUser(seasonId);
  }
}
