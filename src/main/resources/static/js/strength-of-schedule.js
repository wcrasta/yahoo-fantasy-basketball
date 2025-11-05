$(document).ready(function () {

  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  $('#season').on('change', function () {
    var seasonId = $(this).val();
    $('#league').empty().append('<option disabled="disabled" selected="selected">Loading leagues...</option>');
    $('#sos-table tbody').html('<tr class="table-secondary text-center"><td colspan="3">Bir lig seçin.</td></tr>');

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
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">Loading strength of schedule. This may take a while...</td></tr>');

    $.get("/leagues/" + leagueId + "/sos-info", function (data) {
      
      var teams = data.teams;

      teams.sort(function(a, b) {
        if (a.strengthOfSchedule === 0.0) return 1;
        if (b.strengthOfSchedule === 0.0) return -1;
        return b.strengthOfSchedule - a.strengthOfSchedule;
      });

      $tableBody.empty();

      if (teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">Takım verisi bulunamadı.</td></tr>');
        return;
      }

      var rank = 1;
      $.each(teams, function (index, team) {
        var sosScore = team.strengthOfSchedule;
        var scoreDisplay = sosScore.toFixed(3); 
        var rowClass = '';

        if (sosScore === 0.0) {
            scoreDisplay = "N/A (Playoff/Sezon Bitti)";
            rowClass = 'table-secondary';
        } else {
            if (index === 0) {
                rowClass = 'bg-danger-dark';
            } else if (index === 1) {
                rowClass = 'bg-danger-medium';
            } else if (index === 2) {
                rowClass = 'bg-danger-light';
            }
            else if (index === teams.length - 1) {
                rowClass = 'bg-success-dark';
            } else if (index === teams.length - 2) {
                rowClass = 'bg-success-medium';
            } else if (index === teams.length - 3) {
                rowClass = 'bg-success-light';
            }
        }

        var row = '<tr class="' + rowClass + '">' +
          '<td>' + (rank++) + '</td>' +
          '<td>' +
            ' ' + team.name +
          '</td>' +
          '<td>' + scoreDisplay + '</td>' +
          '</tr>';
        
        $tableBody.append(row);
      });
    });
  });

});