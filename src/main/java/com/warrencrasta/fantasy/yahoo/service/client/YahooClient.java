package com.warrencrasta.fantasy.yahoo.service.client;

import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyContentDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyResponseDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class YahooClient {

  private final WebClient webClient;
  private final String yahooBaseUri;

  public YahooClient(WebClient webClient, @Value("${yahoo.base.uri}") String yahooBaseUri) {
    this.webClient = webClient;
    this.yahooBaseUri = yahooBaseUri;
  }

  public FantasyContentDTO getFantasyContent(Map<String, String> uriVariables, String path) {
    var fullFantasyUri =
        UriComponentsBuilder.fromUriString(yahooBaseUri)
            .path(path)
            .queryParam("format", "json_f")
            .buildAndExpand(uriVariables)
            .toUri();

    return Objects.requireNonNull(webClient
        .get()
        .uri(fullFantasyUri)
        .retrieve()
        .bodyToMono(FantasyResponseDTO.class)
        .block())
        .getFantasyContent();
  }

  public FantasyContentDTO getFantasyContent(String path) {
    return getFantasyContent(new HashMap<>(), path);
  }
}
