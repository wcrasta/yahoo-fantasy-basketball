$(document).ready(function () {

  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  $('#season').on('change', function () {
    var seasonId = $(this).val();
    $('#league').empty().append('<option disabled="disabled" selected="selected">Loading leagues...</option>');
    $('#pr-table tbody').html('<tr class="table-secondary text-center"><td colspan="4">Bir lig seçin.</td></tr>');

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

  $('#league').on('change', function () {
    var leagueId = $(this).val();
    var $tableBody = $('#pr-table tbody');
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Güç sıralaması hesaplanıyor... Lütfen bekleyin.</td></tr>');

    $.get("/leagues/" + leagueId + "/power-rankings", function (data) {
      
      var teams = data;

      $tableBody.empty();

      if (teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Takım verisi bulunamadı.</td></tr>');
        return;
      }
      
      $.each(teams, function (index, team) {
        
        var winRate = team.winRate;
        var scoreDisplay = winRate.toFixed(3);
        var rowClass = '';

        if (index === 0) { rowClass = 'bg-success-dark'; }
        else if (index === 1) { rowClass = 'bg-success-medium'; }
        else if (index === 2) { rowClass = 'bg-success-light'; }
        else if (index === teams.length - 1) { rowClass = 'bg-danger-dark'; }
        else if (index === teams.length - 2) { rowClass = 'bg-danger-medium'; }
        else if (index === teams.length - 3) { rowClass = 'bg-danger-light'; }


        var won = team.totalCategoriesWon;
        var tied = team.totalCategoriesTied;
        var played = team.totalCategoriesPlayed;
        
        var lost = played - won - tied;
        
        var recordString = `${won} / ${tied} / ${Math.round(lost)}`;


        var row =
          `<tr class="${rowClass}">
            <td>${index + 1}</td>
            <td>
              <img src="${team.logoUrl}" alt="" class="team-logo"> 
              ${team.name}
            </td>
            <td><b>${scoreDisplay}</b></td>
            <td>${recordString}</td> </tr>`;
        
        $tableBody.append(row);
      });
    });
  });

});