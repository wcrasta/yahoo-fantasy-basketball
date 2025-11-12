$(document).ready(function () {

  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  var dataTableInstance = null;

  $('#season').on('change', function () {
    var seasonId = $(this).val();
    $('#league').empty().append('<option disabled="disabled" selected="selected">Loading leagues...</option>');
    var $tableBody = $('#pr-table tbody');

    if ($.fn.DataTable.isDataTable('#pr-table')) {
      dataTableInstance.destroy();
    }
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Select a league to continue.</td></tr>');

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

    if ($.fn.DataTable.isDataTable('#pr-table')) {
      dataTableInstance.destroy();
    }
    
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Calculating Power Rankings... Please wait.</td></tr>');

    $.get("/leagues/" + leagueId + "/power-rankings", function (data) {
      var teams = data;
      $tableBody.empty();

      if (!teams || teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">No data found for this league.</td></tr>');
        return;
      }

      $.each(teams, function (index, team) {
        
        var rowClass = ''; 

        var winRate = team.winRate;
        var winRateStr = winRate ? winRate.toFixed(3) : "0.000";
        if (winRateStr.startsWith("0")) {
             winRateStr = winRateStr.substring(1); 
        }

        var won = team.totalCategoriesWon;
        var tied = team.totalCategoriesTied;
        var played = team.totalCategoriesPlayed;
        var lost = played - won - tied;
        var recordString = `${won} / ${Math.round(lost)} / ${tied}`; 

        if (index === 0) { rowClass = 'bg-success-dark'; }
        else if (index === 1) { rowClass = 'bg-success-medium'; }
        else if (index === 2) { rowClass = 'bg-success-light'; }
        else if (index === teams.length - 1) { rowClass = 'bg-danger-dark'; }
        else if (index === teams.length - 2) { rowClass = 'bg-danger-medium'; }
        else if (index === teams.length - 3) { rowClass = 'bg-danger-light'; }

        var row =
          `<tr class="${rowClass}">
            <td>${index + 1}</td>
            <td>${team.name}</td>
            <td><b>${winRateStr}</b></td>
            <td>${recordString}</td>
          </tr>`;
        
        $tableBody.append(row);
      });

      dataTableInstance = $('#pr-table').DataTable({
        "paging": false,
        "info": false,
        "autoWidth": false,
        "order": [[ 0, "asc" ]] 
      });
    });
  });

});