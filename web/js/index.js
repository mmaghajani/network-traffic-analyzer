// Load Headers
function loadCss() {
    loadGlobalCss();
    if ($('#css-en')[0] !== undefined) {
        $('#css-en').remove()
    }
    if ($('#css-fa')[0] !== undefined) {
        $('#css-fa').remove()
    }
    $('head').append('<link id="css-en" rel="stylesheet" href="css/index.css">');
    if (Lang == 'fa') {
        $('head').append('<link id="css-fa" rel="stylesheet" href="css/index-fa.css" type="text/css" />');
    }
}

function loadContent() {
    loadGlobalContent();
    if (Lang == 'fa') {
        $('#tab-button-upload').html('بارگذاری');
        $('#text-container').html('<h5 >با استفاده از سرویس "پایش ترافیک شبکه" شما قوانین حریم شخصی در سرویس ما را تایید میکنید و به ما اجازه میدهید که\n فایل های بارگذاری شده شما را تحلیل و بررسی نماییم.</h5>');
        $('#submit-button').attr('value', 'بارگذاری و پایش');
        $('#error-panel-header').html('خطا!');
        $('#error-close').addClass('w3-display-topleft');
    } else {
        $('#tab-button-upload').html('Upload');
        $('#text-container').html('<h5>By using Network Traffic Scanner you consent to our Terms of Service and allow us to share your\n' +
                '                        submission with the data analysis community.</h5>');
        $('#submit-button').attr('value', 'Upload and Scan file');
        $('#error-panel-header').html('Error!');
        $('#error-close').addClass('w3-display-topright');
    }
}

function openTab(tabName) {
    var i;
    var x = document.getElementsByClassName("tab");
    for (i = 0; i < x.length; i++) {
        x[i].style.display = "none";
    }
    document.getElementById(tabName).style.display = "block";

}


$(function () {
    $('#submit-button').click(function () {
            $('#fileToUpload').click();
    });
});

var uploaded_file;

$(function () {
    $('#submit').click(function (event) {
        $("#loading-icon").css("display", "block");
        $("#submit-button").css("display", "none");

        //stop submit the form, we will post it manually.
        event.preventDefault();
        // Get form
        var form = $('form')[0];
        // Create an FormData object
        var data = new FormData(form);

        // If you want to add an extra field for the FormData
        var file = uploaded_file;
        var type = file.type;
        var name = file.name;

        var reader = new FileReader();
        reader.onload = function () {
            setTimeout(function () {
                data.delete("file");
                data.append("file", uploaded_file, name);
                // disabled the submit button
                $("#submit").prop("disabled", true);
                $.ajax({
                    type: "POST",
                    enctype: 'multipart/form-data',
                    url: "upload?lang=fa",
                    data: data,
                    processData: false,
                    contentType: false,
                    cache: false,
                    timeout: 600000,
                    success: function (data, textStatus, request) {
                        $("#loading-icon").css("display", "none");
                        $("#submit-button").css("display", "inline-block");
                        window.location.assign("/report.jsp");
                    },
                    error: function (request, textStatus, errorThrown) {
                        $("#loading-icon").css("display", "none");
                        $("#submit-button").css("display", "inline-block");
                        var error;
                        if (Lang === "fa") {
                            error = 'خطا در ارسال فایل';
                        } else {
                            error = 'An error occurred in file sending';
                        }
                        printError([error]);
                    }
                });

            }, 1000);

        }
            reader.readAsArrayBuffer(file);
        

    });
});

function getUploadLimitFromHeader() {
    var req = new XMLHttpRequest();
    req.open('GET', document.location, false);
    req.send(null);
    return req.getResponseHeader('Upload-Limit');
}

$(document).ready(function () {
    UploadFileSizeLimit = getUploadLimitFromHeader();
    UploadFileSizeLimitStr = Math.floor(UploadFileSizeLimit / 1024 / 1024);

    $('#tab-button-upload').addClass('active-tab');
    $('#fileToUpload').on('change', function () {
        var file = this.files[0];
        uploaded_file = file;
        var reader = new FileReader();
        if (file.size < UploadFileSizeLimit) {
            $('#submit').click();
        } else {
            var errorString = '';
            if (Lang === 'fa') {
                errorString = 'حجم فایل باید کمتر از ' + UploadFileSizeLimitStr + ' مگابایت باشد.';
            } else {
                errorString = 'File size must be less than ' + UploadFileSizeLimitStr + ' MB.';
            }
            var error = [errorString];
            printError(error);
        }
    });

    $('#tab-button-upload').on('touchstart click', function () {
        $('#tab-button-upload').addClass('active-tab');
        $('#tab-button-search').removeClass('active-tab');

    });

    $('#tab-button-search').on('touchstart click', function () {
        $('#tab-button-search').addClass('active-tab');
        $('#tab-button-upload').removeClass('active-tab');

    });

    loadCss();
    loadContent();
});




