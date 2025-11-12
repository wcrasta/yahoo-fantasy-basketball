$(document).ready(function () {

  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  $('#season').on('change', function () {
    var seasonId = $(this).val();
    $('#league').empty().append('<option disabled="disabled" selected="selected">Loading leagues...</option>');
    $('#live-standings-table tbody').html('<tr class="table-secondary text-center"><td colspan="4">Select a League</td></tr>');
    
    if ($.fn.DataTable.isDataTable('#live-standings-table')) {
      $('#live-standings-table').DataTable().destroy();
    }

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

  var dataTableInstance = null;

  $('#league').on('change', function () {
    var leagueId = $(this).val();
    var $tableBody = $('#live-standings-table tbody');

    if ($.fn.DataTable.isDataTable('#live-standings-table')) {
      dataTableInstance.destroy();
    }

    $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Live Standings Calculating...</td></tr>');

    $.get("/leagues/" + leagueId + "/live-standings", function (data) {
      var teams = data;
      $tableBody.empty();
      if (!teams || teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Data not found.</td></tr>');
        return;
      }

      $.each(teams, function (index, team) {
        
        var rowClass = ''; 

        var won = team.totalCategoriesWon;
        var tied = team.totalCategoriesTied;
        var played = team.totalCategoriesPlayed;
        var lost = played - won - tied;
        var recordString = `${won} / ${Math.round(lost)} / ${tied}`;

        var winRateStr = team.winRate.toFixed(3);
        if (winRateStr.startsWith("0")) {
             winRateStr = winRateStr.substring(1);
        }

        if (index === 0) { rowClass = 'bg-success-dark'; }
        else if (index === 1) { rowClass = 'bg-success-medium'; }
        else if (index === 2) { rowClass = 'bg-success-light'; }
        else if (index === teams.length - 1) { rowClass = 'bg-danger-dark'; }
        else if (index === teams.length - 2) { rowClass = 'bg-danger-medium'; }
        else if (index === teams.length - 3) { rowClass = 'bg-danger-light'; }

        var row =
          `<tr class="${rowClass}">
            <td>${index + 1}</td>
            <td>
              ${team.name}
            </td>
            <td>${recordString}</td>
            <td><b>${winRateStr}</b></td>
          </tr>`;
        
        $tableBody.append(row);
      });


      dataTableInstance = $('#live-standings-table').DataTable({
        "paging": false,
        "info": false,
        "autoWidth": false,
        "order": [[ 0, "asc" ]] 
      });
    });
  });
});