package com.warrencrasta.fantasy.yahoo.service.core.livestandings;

import com.warrencrasta.fantasy.yahoo.domain.stat.StatCategory;
import com.warrencrasta.fantasy.yahoo.domain.team.YahooTeam;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.FantasyContentDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.MatchupWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.TeamDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.TeamWrapperDTO;
import com.warrencrasta.fantasy.yahoo.dto.external.yahoo.YahooMatchupDTO;
import com.warrencrasta.fantasy.yahoo.service.client.YahooClient;
import com.warrencrasta.fantasy.yahoo.service.core.league.LeagueService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LiveStandingsService {

  private final YahooClient yahooClient;
  private final LeagueService leagueService;

  public LiveStandingsService(YahooClient yahooClient, LeagueService leagueService) {
    this.yahooClient = yahooClient;
    this.leagueService = leagueService;
  }

  public List<YahooTeam> getLiveStandings(String leagueId) {
    // 1. LİG AYARLARINI ÇEK (Hangi kategoriler var, hangileri ters orantılı?)
    List<StatCategory> relevantCategories = leagueService.getRelevantCategories(leagueId);

    // 2. RESMİ PUAN DURUMUNU ÇEK
    Map<String, String> uriVariables = new HashMap<>();
    uriVariables.put("league_key", leagueId);
    
    FantasyContentDTO standingsContent = yahooClient.getFantasyContent(
        uriVariables, "/league/{league_key}/standings");
    String currentWeek = standingsContent.getLeague().getCurrentWeek();
    List<TeamWrapperDTO> teamsData = standingsContent.getLeague().getStandings().getTeams();

    // Takımları haritaya doldur (Resmi sonuçlarla başla)
    Map<String, YahooTeam> liveTeamsMap = new HashMap<>();
    for (TeamWrapperDTO wrapper : teamsData) {
      var teamDTO = wrapper.getTeam();
      var outcomes = teamDTO.getTeamStandings().getOutcomeTotals();

      YahooTeam liveTeam = new YahooTeam();
      // DİKKAT: teamKey null geliyorsa .getId() kullanın demiştik. 
      // Burada API'den taze çekiyoruz, teamKey dolu olabilir ama garanti olsun diye ID kullanalım.
      // Eğer TeamDTO'da 'id' yoksa 'teamKey' kullanın.
      liveTeam.setId(teamDTO.getTeamKey()); 
      liveTeam.setName(teamDTO.getName());
      
      // Resmi sonuçları ekle
      liveTeam.addWeeklyWins(Double.parseDouble(outcomes.getWins()));
      liveTeam.addWeeklyTies(Double.parseDouble(outcomes.getTies()));
      int played = Integer.parseInt(outcomes.getWins())  
                    + Integer.parseInt(outcomes.getLosses()) 
                    + Integer.parseInt(outcomes.getTies());
      liveTeam.addWeeklyCategoriesPlayed(played);
      
      liveTeamsMap.put(liveTeam.getId(), liveTeam);
    }

    // 3. CANLI SKORLARI ÇEK (O haftanın scoreboard'u)
    FantasyContentDTO scoreboardContent = yahooClient.getFantasyContent(
        uriVariables, "/league/{league_key}/scoreboard;week=" + currentWeek);
    
    // Eğer sezon bitmişse veya maç yoksa direkt resmi sonuçları döndür
    if (scoreboardContent.getLeague().getScoreboard() == null 
        || scoreboardContent.getLeague().getScoreboard().getMatchups() == null) {
      return finalizeStandings(liveTeamsMap);
    }

    List<MatchupWrapperDTO> matchups = scoreboardContent.getLeague().getScoreboard().getMatchups();

    // 4. CANLI SKORLARI HESAPLA VE EKLE
    for (MatchupWrapperDTO matchupWrapper : matchups) {
      YahooMatchupDTO matchup = matchupWrapper.getMatchup();
      
      // Bir eşleşmede 2 takım vardır
      TeamDTO team1 = matchup.getTeams().get(0).getTeam();
      TeamDTO team2 = matchup.getTeams().get(1).getTeam();

      // Bu iki takımı karşılaştır
      double[] results = compareLiveTeams(team1, team2, relevantCategories);
      // results[0] = Team1'in kazandığı
      // results[1] = Team2'nin kazandığı
      // results[2] = Berabere

      // Haritadan bizim canlı takım nesnelerimizi bul
      YahooTeam liveTeam1 = liveTeamsMap.get(team1.getTeamKey());
      YahooTeam liveTeam2 = liveTeamsMap.get(team2.getTeamKey());

      if (liveTeam1 != null && liveTeam2 != null) {
        // CANLI SONUÇLARI MEVCUTLARA EKLE
        liveTeam1.addWeeklyWins(results[0]);
        liveTeam1.addWeeklyTies(results[2]);
        liveTeam1.addWeeklyCategoriesPlayed(relevantCategories.size());

        liveTeam2.addWeeklyWins(results[1]);
        liveTeam2.addWeeklyTies(results[2]);
        liveTeam2.addWeeklyCategoriesPlayed(relevantCategories.size());
      }
    }

    // 5. SONUÇLARI DÖNDÜR
    return finalizeStandings(liveTeamsMap);
  }

  // Yardımcı Metot: İki takımın o anki istatistiklerini karşılaştırır
  private double[] compareLiveTeams(TeamDTO team1, TeamDTO team2, List<StatCategory> categories) {
    double wins1 = 0;
    double wins2 = 0;
    double ties = 0;

    // Her kategori için döngü
    for (StatCategory cat : categories) {
      // Takımların o kategorideki anlık değerini bul
      String val1Str = findStatValue(team1, cat.getId());
      String val2Str = findStatValue(team2, cat.getId());

      // Değerler sayısal mı kontrol et (Bazen '-' dönebilir maç başlamadıysa)
      if (isNumeric(val1Str) && isNumeric(val2Str)) {
        double val1 = Double.parseDouble(val1Str);
        double val2 = Double.parseDouble(val2Str);

        int comparison = Double.compare(val1, val2);
        if (comparison == 0) {
          ties++;
        } else if (cat.isBad()) {
          // Kötü kategori (örn: TO): Düşük olan kazanır
          if (val1 < val2) {
            wins1++;
          } else {
            wins2++;
          }
        } else {
          // İyi kategori (örn: Sayı): Yüksek olan kazanır
          if (val1 > val2) {
            wins1++;
          } else {
            wins2++;
          } 
        }
      } else {
        // Veri yoksa berabere sayalım (veya 0-0)
        ties++;
      }
    }

    return new double[]{wins1, wins2, ties};
  }

  // Yardımcı Metot: TeamDTO içinden belirli bir stat ID'sinin değerini bulur
  private String findStatValue(TeamDTO team, String statId) {
    return team.getTeamStats().getStats().stream()
            .filter(s -> s.getStat().getStatId().equals(statId))
            .findFirst()
            .map(s -> s.getStat().getValue())
            .orElse("-");
  }

  // Yardımcı Metot: Sayısal kontrol
  private boolean isNumeric(String str) {
    if (str == null || str.equals("-")) {
      return false;
    } 
    try {
      Double.parseDouble(str);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  // Yardımcı Metot: Listeyi hazırlar ve sıralar
  private List<YahooTeam> finalizeStandings(Map<String, YahooTeam> teamsMap) {
    List<YahooTeam> liveStandings = new ArrayList<>(teamsMap.values());
    for (YahooTeam team : liveStandings) {
      team.calculateFinalWinRate();
    }
    // Win Rate'e göre büyükten küçüğe sırala
    liveStandings.sort(Comparator.comparingDouble(YahooTeam::getWinRate).reversed());
    return liveStandings;
  }
}