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

function toBitArrayCodec(bytes) {
    var out = [], i, tmp = 0;
    for (i = 0; i < bytes.length; i++) {
        tmp = tmp << 8 | bytes[i];
        if ((i & 3) === 3) {
            out.push(tmp);
            tmp = 0;
        }
    }
    if (i & 3) {
        out.push(sjcl.bitArray.partial(8 * (i & 3), tmp));
    }
    return out;
}

/** Convert from a bitArray to an array of bytes. */
function fromBitArrayCodec(arr) {
    var out = [], bl = sjcl.bitArray.bitLength(arr), i, tmp;
    for (i = 0; i < bl / 8; i++) {
        if ((i & 3) === 0) {
            tmp = arr[i / 4];
        }
        out.push(tmp >>> 24);
        tmp <<= 8;
    }
    return out;
}

function arrayByteToBase64(bytes) {
    var binary = '';
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
        binary += String.fromCharCode(bytes[ i ]);
    }
    return window.btoa(binary);
}

function convertToBase64Bits(input) {
    var bytes = new Uint8Array(input);
    var base64Bits = arrayByteToBase64(bytes);
    return base64Bits;
}

function intToByte(input) {
    // we want to represent the input as a 8-bytes array
    var byteArray = [0, 0, 0, 0];
    var integer = input
    for (var index = 0; index < byteArray.length; index++) {
        var byte = integer & 0xff;
        byteArray [ index ] = byte;
        integer = (integer - byte) / 256;
    }

    return byteArray;
}
;

function conv(input) {
    var uint8array = new Array();
    for (var i = 0; i < input.length; i++) {
        var bytes = intToByte(input[i])
        for (var j = bytes.length - 1; j >= 0; j--) {
            uint8array.push(bytes[j])
        }
    }
    return uint8array;
}

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

//        rsaEncrypt = new JSEncrypt();
//        publicKey = getPublicKey();
//        rsaEncrypt.setPublicKey(publicKey);

//        var symmKey = new Uint32Array(sjcl.random.randomWords(6, 4));
//        var convSymmKey = conv(symmKey);
//        var base64BitsOfSymmKey = convertToBase64Bits(convSymmKey);

        var reader = new FileReader();
        reader.onload = function () {
//            var base64BitsFile = convertToBase64Bits(this.result);
            setTimeout(function () {
//                var encryptedData = CryptoJS.AES.encrypt(base64BitsFile, base64BitsOfSymmKey, {padding: CryptoJS.pad.Pkcs7});
//
//                var encryptedKey = rsaEncrypt.encrypt(encryptedData.key.toString());
//                data.delete('key');
//                data.append("key", encryptedKey);
//                var encryptedIV = rsaEncrypt.encrypt(encryptedData.iv.toString());
//                data.append("iv", encryptedIV);

//                var encryptedFile = new Blob([encryptedData.ciphertext.toString()], {type: type});
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




