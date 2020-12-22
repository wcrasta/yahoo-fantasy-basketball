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
      }, function(data) {
        var leaguesDropdownHtml = '<option value="">Select a League</option>';
        for (var i = 0; i < data.length; i++) {
          leaguesDropdownHtml += '<option value="' + data[i].id + '">' + data[i].name + '</option>';
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
    		for (var i = 0; i < weeks.length; i++) {
    		    weeksDropdownHtml += '<option value="' + weeks[i].split(" ", 2)[1] + '">' + weeks[i] + '</option>';
    		}
    		weeksDropdownHtml += '</option>';
    		$('#week').html(weeksDropdownHtml);
        });
    });

    $('#week').change(function(){
        weekNum = $(this).val();
        var teamDropdownHtml = '<option value="">Select a Team</option>';
        for (var i = 0; i < teams.length; i++) {
            teamDropdownHtml += '<option value="' + teams[i].id + '">' + teams[i].name + '</option>';
        }
        teamDropdownHtml += '</option>';
        $('#team').html(teamDropdownHtml);
    });

    $('#team').change(function(){
        teamId = $(this).val();
        var url = "/leagues/" + leagueId + "/weekly-matchups?teamId=" + teamId + "&week=" + weekNum;
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
                        "render": function ( data, type, row, meta ) {
                            var len = data.length;
                            if (len == 0) {
                                return data;
                            }
                            return len + " (" + data.join(', ') + ")";
                        }
                    },
                    {
                        "data": "categoriesLost",
                        "render": function ( data, type, row, meta ) {
                            var len = data.length;
                            if (len == 0) {
                                return data;
                            }
                            return len + " (" + data.join(', ') + ")";
                        }
                    },
                    {
                        "data": "categoriesTied",
                        "render": function ( data, type, row, meta ) {
                            var len = data.length;
                            if (len == 0) {
                                return data;
                            }
                            return len + " (" + data.join(', ') + ")";
                        }
                    }
                ]
            });
    });
});

$("#menu-toggle").click(function(e) {
  e.preventDefault();
  $("#wrapper").toggleClass("toggled");
});