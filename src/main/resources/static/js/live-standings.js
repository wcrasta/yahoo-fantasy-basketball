$(document).ready(function () {

  // Menu toggle
  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  // Sezon seçildiğinde Lig'leri doldur
  $('#season').on('change', function () {
    var seasonId = $(this).val();
    $('#league').empty().append('<option disabled="disabled" selected="selected">Loading leagues...</option>');
    $('#live-standings-table tbody').html('<tr class="table-secondary text-center"><td colspan="4">Bir lig seçin.</td></tr>');

    $.get("/seasons/" + seasonId + "/leagues", function (data) {
      var $leagueDropdown = $('#league');
      $leagueDropdown.empty().append('<option disabled="disabled" selected="selected">Select a League</option>');
      $.each(data, function (index, league) {
        $leagueDropdown.append($('<option>', {
          value: league.id,
          text: league.name
        }));
      });
    });
  });

  // Lig seçildiğinde Live Standings tablosunu doldur
  $('#league').on('change', function () {
    var leagueId = $(this).val();
    var $tableBody = $('#live-standings-table tbody');
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Canlı puan durumu hesaplanıyor...</td></tr>');

    // API ÇAĞRISI
    $.get("/leagues/" + leagueId + "/live-standings", function (data) {
      var teams = data; // Sıralanmış takım listesi

      $tableBody.empty();

      if (!teams || teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Veri bulunamadı.</td></tr>');
        return;
      }

      $.each(teams, function (index, team) {
        // Derece Hesaplama (K-B-M)
        var won = team.totalCategoriesWon;
        var tied = team.totalCategoriesTied;
        var played = team.totalCategoriesPlayed;
        var lost = played - won - tied;
        var recordString = `${won} - ${tied} - ${Math.round(lost)}`;

        // Win Rate (.XYZ formatı)
        var winRateStr = team.winRate.toFixed(3);
        if (winRateStr.startsWith("0")) {
             winRateStr = winRateStr.substring(1); // "0.583" yerine ".583" yapmak için
        }

        // Renklendirme (Playoff potası vb. için basit bir mantık)
        // İlk 6 takım yeşilimsi, diğerleri normal olsun şimdilik.
        var rowClass = '';
        if (index < 6) {
             // rowClass = 'table-success'; // İsterseniz açabilirsiniz
        }

        var row =
          `<tr class="${rowClass}">
            <td>${index + 1}</td>
            <td>
              <img src="${team.logoUrl}" alt="" class="team-logo">
              ${team.name}
            </td>
            <td>${recordString}</td>
            <td><b>${winRateStr}</b></td>
          </tr>`;
        
        $tableBody.append(row);
      });
    });
  });
});