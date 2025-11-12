$(document).ready(function () {

  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  var dataTableInstance = null;

  $('#season').on('change', function () {
    var seasonId = $(this).val();
    $('#league').empty().append('<option disabled="disabled" selected="selected">Loading leagues...</option>');
    var $tableBody = $('#sos-table tbody');

    if ($.fn.DataTable.isDataTable('#sos-table')) {
      dataTableInstance.destroy();
    }
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">Select a league to continue.</td></tr>');

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
    var $tableBody = $('#sos-table tbody');

    if ($.fn.DataTable.isDataTable('#sos-table')) {
      dataTableInstance.destroy();
    }
    
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">Calculating Strength of Schedule... Please wait.</td></tr>');

    $.get("/leagues/" + leagueId + "/sos-info", function (data) {
      
      var teams = data.teams; 
      
      teams.sort(function(a, b) {
        var scoreA = a.strengthOfSchedule || 0;
        var scoreB = b.strengthOfSchedule || 0;
        return scoreB - scoreA;
      });

      $tableBody.empty();

      if (!teams || teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">No data found for this league.</td></tr>');
        return;
      }

      $.each(teams, function (index, team) {
        
        var rowClass = ''; 

        var sosScore = team.strengthOfSchedule;
        var scoreDisplay = ".000"; 

        if (sosScore != null && sosScore > 0) {
            scoreDisplay = sosScore.toFixed(3); 
            if (scoreDisplay.startsWith("0")) {
                scoreDisplay = scoreDisplay.substring(1); 
            }
        } else {
            scoreDisplay = "N/A (Playoff/Season End)";
        }

        if (sosScore > 0) { 
            if (index === 0) { rowClass = 'bg-danger-dark'; }    
            else if (index === 1) { rowClass = 'bg-danger-medium'; }
            else if (index === 2) { rowClass = 'bg-danger-light'; }  
            else if (index === teams.length - 1) { rowClass = 'bg-success-dark'; }    
            else if (index === teams.length - 2) { rowClass = 'bg-success-medium'; } 
            else if (index === teams.length - 3) { rowClass = 'bg-success-light'; }  
        } else {
            rowClass = 'table-secondary'; 
        }
        
        var row =
          `<tr class="${rowClass}">
            <td>${index + 1}</td>
            <td>${team.name}</td>
            <td><b>${scoreDisplay}</b></td>
          </tr>`;
        
        $tableBody.append(row);
      });


      dataTableInstance = $('#sos-table').DataTable({
        "paging": false,
        "info": false,
        "autoWidth": false,
        "order": [[ 2, "desc" ]] 
      });
    });
  });

});