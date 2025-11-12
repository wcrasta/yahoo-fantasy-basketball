$(document).ready(function () {

  // Menu toggle
  $("#menu-toggle").click(function (e) {
    e.preventDefault();
    $("#wrapper").toggleClass("toggled");
  });

  // DataTable örneğini saklamak için değişken
  var dataTableInstance = null;

  // Sezon seçildiğinde
  $('#season').on('change', function () {
    var seasonId = $(this).val();
    $('#league').empty().append('<option disabled="disabled" selected="selected">Loading leagues...</option>');
    var $tableBody = $('#pr-table tbody');

    // Lig değiştirmeden önce mevcut DataTables'ı yok et
    if ($.fn.DataTable.isDataTable('#pr-table')) {
      dataTableInstance.destroy();
    }
    // Tabloyu temizle
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Select a league to continue.</td></tr>');

    // Ligleri doldur
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

  // Lig seçildiğinde
  $('#league').on('change', function () {
    var leagueId = $(this).val();
    var $tableBody = $('#pr-table tbody');

    // 1. Önceki DataTables'ı yok et
    if ($.fn.DataTable.isDataTable('#pr-table')) {
      dataTableInstance.destroy();
    }
    
    // Yükleme mesajı
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">Calculating Power Rankings... Please wait.</td></tr>');

    // API'den Power Rankings verisini çek
    $.get("/leagues/" + leagueId + "/power-rankings", function (data) {
      var teams = data;
      $tableBody.empty(); // Tabloyu temizle

      if (!teams || teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="4">No data found for this league.</td></tr>');
        return;
      }

      // ==========================================================
      // >> DÖNGÜ İÇİ DÜZELTİLDİ VE DOLDURULDU <<
      // ==========================================================
      $.each(teams, function (index, team) {
        
        // --- HATA ÇÖZÜMÜ: rowClass burada tanımlanmalı ---
        var rowClass = ''; 

        // 1. Win Rate (.XYZ) Hesaplaması
        var winRate = team.winRate;
        // '.toFixed' hatasını önlemek için null kontrolü yap
        var winRateStr = winRate ? winRate.toFixed(3) : "0.000";
        if (winRateStr.startsWith("0")) {
             winRateStr = winRateStr.substring(1); // ".672" formatı için
        }

        // 2. Derece (K-B-M) Hesaplaması
        var won = team.totalCategoriesWon;
        var tied = team.totalCategoriesTied;
        var played = team.totalCategoriesPlayed;
        var lost = played - won - tied; // Kaybedileni hesapla
        var recordString = `${won} / ${Math.round(lost)} / ${tied}`; // "304 / 9 / 145"

        // 3. Renklendirme (İstek: En iyi = Yeşil, En kötü = Kırmızı)
        if (index === 0) { rowClass = 'bg-success-dark'; }
        else if (index === 1) { rowClass = 'bg-success-medium'; }
        else if (index === 2) { rowClass = 'bg-success-light'; }
        else if (index === teams.length - 1) { rowClass = 'bg-danger-dark'; }
        else if (index === teams.length - 2) { rowClass = 'bg-danger-medium'; }
        else if (index === teams.length - 3) { rowClass = 'bg-danger-light'; }

        // 4. HTML Satırını Oluştur (İstek: Logo olmadan)
        var row =
          `<tr class="${rowClass}">
            <td>${index + 1}</td>
            <td>${team.name}</td>
            <td><b>${winRateStr}</b></td>
            <td>${recordString}</td>
          </tr>`;
        
        $tableBody.append(row);
      });
      // ==========================================================
      // >> DÜZELTME SONU <<
      // ==========================================================

      // 2. DataTables'ı başlat
      dataTableInstance = $('#pr-table').DataTable({
        "paging": false,
        "info": false,
        "autoWidth": false,
        "order": [[ 0, "asc" ]] // Sıra'ya (Rank) göre sıralı başla
      });
    });
  });

});