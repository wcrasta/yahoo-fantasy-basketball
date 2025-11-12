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
    var $tableBody = $('#sos-table tbody');

    // Lig değiştirmeden önce mevcut DataTables'ı yok et
    if ($.fn.DataTable.isDataTable('#sos-table')) {
      dataTableInstance.destroy();
    }
    // Tabloyu temizle (İngilizce metin)
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">Select a league to continue.</td></tr>');

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
    var $tableBody = $('#sos-table tbody');

    // 1. Önceki DataTables'ı yok et
    if ($.fn.DataTable.isDataTable('#sos-table')) {
      dataTableInstance.destroy();
    }
    
    // Yükleme mesajı (İngilizce)
    $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">Calculating Strength of Schedule... Please wait.</td></tr>');

    // API'den SoS verisini çek (Bu '/sos-info' adresini kullandığımızı varsayıyorum)
    $.get("/leagues/" + leagueId + "/sos-info", function (data) {
      
      // SoS, LeagueInfoDTO döndürür, bu yüzden 'data.teams' kullanırız
      var teams = data.teams; 
      
      // ==========================================================
      // >> DÖNGÜ ÖNCESİ VERİYİ SIRALA <<
      // ==========================================================
      // Veriyi 'strengthOfSchedule' puanına göre ZORDAN (yüksek puan) KOLAYA (düşük puan) doğru sırala
      teams.sort(function(a, b) {
        var scoreA = a.strengthOfSchedule || 0;
        var scoreB = b.strengthOfSchedule || 0;
        return scoreB - scoreA; // Yüksek puan (Zor) üste gelsin
      });
      // ==========================================================

      $tableBody.empty(); // Tabloyu temizle

      if (!teams || teams.length === 0) {
        $tableBody.html('<tr class="table-secondary text-center"><td colspan="3">No data found for this league.</td></tr>');
        return;
      }

      // ==========================================================
      // >> DÖNGÜ İÇİ DÜZELTİLDİ VE DOLDURULDU <<
      // ==========================================================
      $.each(teams, function (index, team) {
        
        // --- HATA ÇÖZÜMÜ: rowClass burada tanımlanmalı ---
        var rowClass = ''; 

        // 1. SoS Puanını (.XYZ) Formatla
        var sosScore = team.strengthOfSchedule;
        var scoreDisplay = ".000"; // Varsayılan

        // 'toFixed' hatasını önlemek için null kontrolü yap
        if (sosScore != null && sosScore > 0) {
            scoreDisplay = sosScore.toFixed(3); // "0.521"
            if (scoreDisplay.startsWith("0")) {
                scoreDisplay = scoreDisplay.substring(1); // ".521"
            }
        } else {
            // Sezon sonu/playoff ise
            scoreDisplay = "N/A (Playoff/Season End)";
        }

        // 2. Renklendirme (SoS Mantığı: Yüksek Puan = ZOR = Kırmızı)
        // Veri zaten sıralı olduğu için 'index' kullanabiliriz
        if (sosScore > 0) { // N/A olanları renklendirme
            if (index === 0) { rowClass = 'bg-danger-dark'; }    // En Zor
            else if (index === 1) { rowClass = 'bg-danger-medium'; } // 2. En Zor
            else if (index === 2) { rowClass = 'bg-danger-light'; }  // 3. En Zor
            else if (index === teams.length - 1) { rowClass = 'bg-success-dark'; }    // En Kolay
            else if (index === teams.length - 2) { rowClass = 'bg-success-medium'; } // 2. En Kolay
            else if (index === teams.length - 3) { rowClass = 'bg-success-light'; }  // 3. En Kolay
        } else {
            rowClass = 'table-secondary'; // N/A satırları için
        }
        
        // 3. HTML Satırını Oluştur (Logo olmadan)
        var row =
          `<tr class="${rowClass}">
            <td>${index + 1}</td>
            <td>${team.name}</td>
            <td><b>${scoreDisplay}</b></td>
          </tr>`;
        
        $tableBody.append(row);
      });
      // ==========================================================
      // >> DÜZELTME SONU <<
      // ==========================================================


      // 2. DataTables'ı başlat
      dataTableInstance = $('#sos-table').DataTable({
        "paging": false,
        "info": false,
        "autoWidth": false,
        // Varsayılan sıralama: 3. sütun (SoS Puanı), azalan (desc) - yani Zordan Kolaya
        "order": [[ 2, "desc" ]] 
      });
    });
  });

});