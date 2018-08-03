function myFunction(id) {
    var x = document.getElementById(id);
    if (x.className.indexOf("w3-show") == -1) {
        x.className += " w3-show";
    } else {
        x.className = x.className.replace(" w3-show", "");
    }
}

function loadCss() {
    loadGlobalCss();
    if ($('#css-en')[0] !== undefined) {
        $('#css-en').remove()
    }
    if ($('#css-fa')[0] !== undefined) {
        $('#css-fa').remove()
    }
    $('head').append('<link id="css-en" rel="stylesheet" href="css/error.css">');
    if (Lang == 'fa') {
        $('head').append('<link id="css-fa" rel="stylesheet" href="css/error-fa.css">');
    }
//    
//    loadGlobalCss();
//    if ($('#css-en')[0] !== undefined) {
//        $('#css-en').remove()
//    }
//    $('head').append('<link id="css-en" rel="stylesheet" href="css/error.css">');
}

function loadContent() {
    loadGlobalContent();
    var errorCode = $('#status-error-code').attr('value');
    if (Lang == 'fa') {
        $('#error-description').css('direction', 'rtl');
        switch (errorCode) {
            case "404":
                $('#error-code').html('#۴۰۴');
                $('#error-description').html("صفحه موردنظر شما یافت نشد.");
                break;
            case "401":
                $('#error-code').html('#۴۰۱');
                $('#error-description').html("برای دسترسی به این صفحه ابتدا وارد حساب کاربری خود شوید.");
                break;
            case "0":
            case "403":
                $('#error-code').html('#۴۰۳');
                $('#error-description').html("دسترسی به این صفحه ممنوع می باشد.");
                break;
            case "500":
                $('#error-code').html('#۵۰۰');
                $('#error-description').html("خطای داخلی در سمت سرور رخ داده است. لطفا بعدا دوباره تلاش بفرمایید.");
                break;
            case "503":
                $('#error-code').html('#۵۰۳');
                $('#error-description').html("عملیات مورد نظر پشتیبانی نمی شود.");
                break;
            default:
                $('#error-description').html("خطای غیرمنتظره رخ داده است.");
                break;
        }

    } else {
        switch (errorCode) {
            case "404":
                $('#error-code').html('#404');
                $('#error-description').html("We could not find this page!");
                break;
            case "401":
                $('#error-code').html('#401');
                $('#error-description').html("Please login first then try again.");
                break;
            case "0":
            case "403":
                $('#error-code').html('#403');
                $('#error-description').html("Access to this page is forbidden.");
                break;
            case "500":
                $('#error-code').html('#500');
                $('#error-description').html("An internal error occured. Please try later.");
                break;
            case "503":
                $('#error-code').html('#503');
                $('#error-description').html("Your intended operation is not supported.");
                break;
            default:
                $('#error-description').html("An unexpected error occured.");
                break;
        }
    }
}

$(document).ready(function () {
    loadCss();
    loadContent();
});