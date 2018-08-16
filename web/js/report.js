function loadCss() {
    loadGlobalCss();
    if ($('#css-en')[0] !== undefined) {
        $('#css-en').remove()
    }
    if ($('#css-fa')[0] !== undefined) {
        $('#css-fa').remove()
    }
    $('head').append('<link id="css-en" rel="stylesheet" href="css/report.css">');
    if (Lang == 'fa') {
        $('head').append('<link id="css-fa" rel="stylesheet" href="css/report-fa.css">');
    }
}
var last_scan_date;
var Data;

function loadContent() {
    loadGlobalContent();
    if (Lang == 'fa') {
        $('#fileNameTitle').html('نام فایل ');
        $('#format').html('فرمت ');
        $('#size').html('حجم ');
        $('#id').html('شناسه ');
        $('#originAddress').html('آدرس مبدا ');
        $('#countryTitle').html('کشور ');
        $('#timeTitle').html('زمان پایش');
        $('#rescan').html('پایش مجدد');
        $('#av').html('ردیف داده');
        $('#detection-header').html('تشخیص');
        $('#score1').html('IF');
        $('#score2').html('FastVOA');
        $('#score3').html('LOF');
        $('#score4').html('SVM');
        $('#return').html('بازگشت به صفحه بارگذاری');
        $('#oldBannerContent').html('این فایل قبلا توسط سرویس ما پایش گردیده است. تاریخ پایش قبلی : ' + last_scan_date);
        $('#loading').html('<p class="w3-center">در حال پایش ...</p>\n\
							<p class="w3-center">لطفا شکیبا باشید</p>\n\
							<img src="assets/scanning.gif">');
        $('#rescan').addClass('w3-left');
        $('#rescan').html('پایش مجدد');

        //data styling
        styleTableFA();
    } else {
        $('#fileNameTitle').html(' File Name');
        $('#format').html(' Type');
        $('#size').html(' Size');
        $('#id').html(' Scan ID');
        $('#originAddress').html(' Origin IP');
        $('#countryTitle').html(' Country');
        $('#timeTitle').html(' Time of Scan');
        $('#rescan').html('Rescan');
        $('#av').html('Data Index');
        $('#detection-header').html('Detection');
        $('#score1').html('IF');
        $('#score2').html('FastVOA');
        $('#score3').html('LOF');
        $('#score4').html('SVM');
        $('#return').html('Back to Upload Form');
        $('#oldBannerContent').html('This file was previously scanned by our service. Showing an old report from :' + last_scan_date);
        $('#loading').html('<p class="w3-center">Scan in Progress ...</p>\n\
							<p class="w3-center">Please Wait</p>\n\
							<img src="assets/scanning.gif">');

        $('#rescan').addClass('w3-right');
        $('#rescan').html('Rescan');
        //data styling
        styleTableEN();
    }
}

function styleTableFA() {
    var rows = $($('#result').children()[0]).children();
    for (var i = 1; i < rows.length; i++) {
        var data = $(rows[i]).children();
        for (var j = 0; j < data.length; j++) {
            $(data[j]).addClass('table-content');
            var text = $(data[j]).text().trim();
            if (j === 1) {
                if (Data[i - 1].DETECTION.DEFINITION === 'Clean') {
                    $(data[j]).addClass('w3-text-green');
                    $(data[j]).html('<span><i class="material-icons status-icon">check</i><span class="status"> پاک</span></span>');
                } else if (Data[i - 1].DETECTION.DEFINITION === 'Infected') {
                    $(data[j]).addClass('w3-text-red');
                    $(data[j]).html('<i class="material-icons status-icon">warning</i><span class="status"> آلوده</span>');
                } else if (Data[i - 1].DETECTION.DEFINITION === 'Suspected') {
                    $(data[j]).addClass('w3-text-orange');
                    $(data[j]).html('<div><i class="material-icons status-icon">report</i><span class="status"> مشکوک</span></div>');
                } else if (Data[i - 1].DETECTION.DEFINITION === 'Failed') {
                    $(data[j]).addClass('w3-text-orange');
                    $(data[j]).html('<i class="material-icons status-icon">report</i><span class="status"> ناموفق</span>');
                }
            }
        }
        $($(data[2]).children()[0]).addClass('description');
    }
}

function styleTableEN() {
    var rows = $($('#result').children()[0]).children();
    for (var i = 1; i < rows.length; i++) {
        var data = $(rows[i]).children();
        for (var j = 0; j < data.length; j++) {
            $(data[j]).addClass('table-content');
            var text = $(data[j]).text().trim();
            if (j === 1) {
                if (Data[i - 1].DETECTION.DEFINITION === 'Clean') {
                    $(data[j]).addClass('w3-text-green');
                    $(data[j]).html('<i class="material-icons status-icon">check</i><span class="status"> Clean</span>');
                } else if (Data[i - 1].DETECTION.DEFINITION === 'Infected') {
                    $(data[j]).addClass('w3-text-red');
                    $(data[j]).html('<i class="material-icons status-icon">warning</i><span class="status"> Infected</span>');
                } else if (Data[i - 1].DETECTION.DEFINITION === 'Suspected') {
                    $(data[j]).addClass('w3-text-orange');
                    $(data[j]).html('<i class="material-icons status-icon">report</i><span class="status"> Suspected</span>');
                } else if (Data[i - 1].DETECTION.DEFINITION === 'Failed') {
                    $(data[j]).addClass('w3-text-orange');
                    $(data[j]).html('<i class="material-icons status-icon">report</i><span class="status"> Failed</span>');
                }
            }
        }
    }
}

function addDataToTable() {
    $($($('#result').children()).children()).remove('.table-record');
    $('#scanID').text(Data[0].ANALYSE.ID);
    $('#scanIP').text(Data[0].ANALYSE.IP);
    $('#country').text(Data[0].ANALYSE.COUNTRY);
    $('#time').text(Data[0].ANALYSE.TIME);
    for (var i = 0; i < Data.length; i++) {
        $("#result").append("<tr class='table-record'><td class='av'>" + Data[i].DATA_INDEX + "</td>"
                + "<td class='detection'>" + Data[i].DETECTION.DEFINITION + "</td>"
                + "<td class='Score1'>" + Data[i].SCORE1 + "</td>"
                + "<td class='Score2'>" + Data[i].SCORE2 + "</td>"
                + "<td class='Score3'>" + Data[i].SCORE3 + "</td>"
                + "<td class='Score4'>" + Data[i].SCORE4 + "</td></tr>");
    }
}

function doRescan() {
    var base = $('base').attr('href');
    $.ajax({
        type: 'POST',
        url: base + 'doScan',
        beforeSend: function (xhrObj) {
            $(".page-cover").css("opacity", 0.6).fadeIn(300, function () {
                $('.scanning').css({'position': 'absolute', 'z-index': 9999, 'display': 'block'});
            });
        },
        success: function (data, textStatus, request) {
            $(".page-cover").css("display", 'none');
            $(".scanning").css("display", 'none');
            Data = data;
            addDataToTable();

            if (Lang === 'fa') {
                styleTableFA();
            } else {
                styleTableEN();
            }

            $('#oldReportBanner').css('display', 'none');
        },
        error: function (xhr, textStatus, error) {
            // failed request; give feedback to user
        }
    });
}

function doSearch(base) {
    $.ajax({
        type: 'POST',
        url: base + 'doSearch',

        beforeSend: function (xhrObj) {
            $(".page-cover").css("opacity", 0.6).fadeIn(300, function () {
                $('.scanning').css({'position': 'absolute', 'z-index': 9999});
            });
        },
        success: function (data, textStatus, request) {
            // successful request; do something with the data 
            $(".page-cover").css("display", 'none');
            $(".scanning").css("display", 'none');

            var report = request.getResponseHeader('Report');
            var rescan = request.getResponseHeader('Rescan');
            last_scan_date = request.getResponseHeader('Date');

            if (report === 'old') {
                $('#oldReportBanner').css('display', 'block');
            }
            if (rescan === 'true') {
                $('.rescan-btn').css('display', 'block');
            } else {
                $('.rescan-btn').css('display', 'none');
            }
            Data = data;
            addDataToTable();

            if (Lang === 'fa') {
                styleTableFA();
            } else {
                styleTableEN();
            }

            loadContent()
        },
        error: function (xhr, textStatus, error) {
            var newDoc = document.open("text/html", "replace");
            newDoc.write(xhr.responseText);
            newDoc.close();
        }
    });
}

function loadProperIcon(base) {
    var typeContent = $('#type').text();
    var type = typeContent.split("/")[1].trim();
    $.ajax({
        url: base + 'assets/type_icon/' + type + '.png',
        type: 'HEAD',
        error: function () {
            //file not exists
            type = typeContent.split("/")[0].trim();
            $.ajax({
                url: base + 'assets/type_icon/' + type + '.png',
                type: 'HEAD',
                error: function () {
                    //file not exists
                    var icon = $('#icon').attr('src', base + 'assets/type_icon/' + 'app' + '.png');
                },
                success: function () {
                    //file exists
                    var icon = $('#icon').attr('src', base + 'assets/type_icon/' + type + '.png');
                }
            });
        },
        success: function () {
            //file exists
            var icon = $('#icon').attr('src', base + 'assets/type_icon/' + type + '.png');
        }
    });
}

$(document).ready(function () {
    var base = $('base').attr('href');

    doSearch(base);

    loadProperIcon(base);

    loadCss();
    loadContent();

});