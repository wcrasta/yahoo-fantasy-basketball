$(document).ready(function() {
    var seasonId;
    var leagueId;
    var teams;
    var weekNum;
    var teamId;
    $('#season').change(function(){
      seasonId = $(this).val();
      var url = "/seasons/" + seasonId + "/leagues";
      $.getJSON(url, {
        ajax : 'true'
      }, function(leagues) {
        var leaguesDropdownHtml = '<option value="">Select a League</option>';
        for (let league of leagues) {
          leaguesDropdownHtml += '<option value="' + league.id + '">' + league.name + '</option>';
        }
        leaguesDropdownHtml += '</option>';
        $('#league').html(leaguesDropdownHtml);
      });
    });

    $('#league').change(function(){
      leagueId = $(this).val();
    	var url = "/leagues/" + leagueId + "/info";
    	$.getJSON(url, {
    		ajax : 'true'
    	}, function(data) {
    		var weeksDropdownHtml = '<option value="">Select a Week</option>';
    		var weeks = data.weeks;
    		teams = data.teams;
    		for (let week of weeks) {
            weeksDropdownHtml += '<option value="' + week.split(" ", 2)[1] + '">' + week + '</option>';
        }
    		weeksDropdownHtml += '</option>';
    		$('#week').html(weeksDropdownHtml);
        });
    });

    $('#week').change(function(){
        weekNum = $(this).val();
        var teamDropdownHtml = '<option value="">Select a Team</option>';
        for (let team of teams) {
            teamDropdownHtml += '<option value="' + team.id + '">' + team.name + '</option>';
        }
        teamDropdownHtml += '</option>';
        $('#team').html(teamDropdownHtml);
    });

    $('#team').change(function(){
        teamId = $(this).val();
        var url = "/leagues/" + leagueId + "/weekly-matchups?teamId=" + teamId + "&week=" + weekNum;
        function formatResults (data, type, row, meta) {
          var len = data.length;
          if (len == 0) {
              return data;
          }
          return len + " (" + data.join(', ') + ")";
        }
        $('#matchups').dataTable({
                "bDestroy": true,
                "createdRow": function( row, data, dataIndex ) {
                    if (data["categoriesWon"].length > data["categoriesLost"].length) {
                        $(row).addClass('table-success');
                    } else if (data["categoriesWon"].length < data["categoriesLost"].length) {
                        $(row).addClass('table-danger');
                    } else {
                        $(row).addClass('table-warning');
                    }
                },
                "paging": false,
                "ajax": {
                    "url": url,
                    "dataSrc": ""
                },
                "columns": [
                    {"data": "opponent"},
                    {
                        "data": "categoriesWon",
                        "render": formatResults
                    },
                    {
                        "data": "categoriesLost",
                        "render": formatResults
                    },
                    {
                        "data": "categoriesTied",
                        "render": formatResults
                    }
                ]
            });
    });
});

$("#menu-toggle").click(function(e) {
  e.preventDefault();
  $("#wrapper").toggleClass("toggled");
});