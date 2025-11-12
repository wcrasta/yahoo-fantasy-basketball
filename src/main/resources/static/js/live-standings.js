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
    // Tabloyu da sıfırla
    $('#live-standings-table tbody').html('<tr class="table-secondary text-center"><td colspan="4">Select a League</td></tr>');
    
    // DataTables'ı (eğer varsa) yok et
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

  // Lig seçildiğinde Live Standings tablosunu doldur
  var dataTableInstance = null;

  $('#league').on('change', function () {
    var leagueId = $(this).val();
    var $tableBody = $('#live-standings-table tbody');

    // 1. Önceki DataTables'ı yok et (eğer varsa)
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

      // ==========================================================
      // >> DÖNGÜ İÇİ GÜNCELLENDİ <<
      // ==========================================================
      $.each(teams, function (index, team) {
        
        // rowClass değişkenini burada başlatıyoruz (Hata 2'nin çözümü)
        var rowClass = ''; 

        // 1. Derece (K-B-M) Hesaplaması
        // Java'dan gelen 'totalCategoriesWon', 'totalCategoriesTied', 'totalCategoriesPlayed' kullanılıyor
        var won = team.totalCategoriesWon;
        var tied = team.totalCategoriesTied;
        var played = team.totalCategoriesPlayed;
        // Kaybedileni hesapla: Toplam - (Kazanılan + Berabere)
        var lost = played - won - tied;
        // String olarak formatla: "29 - 0 - 7"
        var recordString = `${won} / ${Math.round(lost)} / ${tied}`;

        // 2. Win Rate (.XYZ) Formatlaması
        var winRateStr = team.winRate.toFixed(3); // "0.806"
        if (winRateStr.startsWith("0")) {
             winRateStr = winRateStr.substring(1); // ".806"
        }

        // 3. Renklendirme (Power Rankings ile aynı)
        // En iyi 3 takım yeşil
        if (index === 0) { rowClass = 'bg-success-dark'; }
        else if (index === 1) { rowClass = 'bg-success-medium'; }
        else if (index === 2) { rowClass = 'bg-success-light'; }
        // En kötü 3 takım kırmızı
        else if (index === teams.length - 1) { rowClass = 'bg-danger-dark'; }
        else if (index === teams.length - 2) { rowClass = 'bg-danger-medium'; }
        else if (index === teams.length - 3) { rowClass = 'bg-danger-light'; }

        // 4. HTML Satırını (Row) Oluşturma
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
      // ==========================================================
      // >> GÜNCELLEME SONU <<
      // ==========================================================


      // 2. DataTables'ı başlat
      dataTableInstance = $('#live-standings-table').DataTable({
        "paging": false,
        "info": false,
        "autoWidth": false,
        "order": [[ 0, "asc" ]] // Sıra'ya (Rank) göre sıralı başla
      });
    });
  });
});