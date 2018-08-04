// Compatibility for IE8
if (!Date.now) {
    Date.now = function () {
        return new Date().getTime();
    }
}

function setLang(lang) {
    localStorage.setItem('Lang', lang);
    Lang = lang;
    loadCss();
    loadContent();
}

function printError(errorArray) {
    $('#error-panel').css('display', 'block');
    $('#error-list').empty();
    for (var i = 0; i < errorArray.length; i++) {
        $('#error-list').append('<li>' + errorArray[i] + '</li>');
    }
}


function loadGlobalCss() {
    $('head').append('<link rel="stylesheet" href="node_modules/w3-css/w3.css">');
    $('head').append('<link rel="stylesheet" href="css/global.css">');
    $('head').append('<link href="node_modules/shabnam-font/dist/font-face.css" rel="stylesheet"\n' +
            '          type="text/css"/>');
}

function loadGlobalContent() {
    addHeader();
    if (Lang == 'fa') {
        $('#faItem').html('فارسی<i class="material-icons material-icons-menu menu-check">check_circle</i>');
        $('#enItem').html('English');
        $('#appName').html('پایش ترافیک شبکه<i class="material-icons logo">security</i>');
    } else {
        $('#enItem').html('<i\n' +
                '                    class="material-icons material-icons-menu menu-check">check_circle</i> English');
        $('#faItem').html('فارسی');
        $('#appName').html('<i class="material-icons logo">security</i>Network Traffic Scanner');
    }
}

function fileSelected(e) {
    var file = e.files[0];
    var reader = new FileReader();
    content = reader.readAsText(file);
    reader.onloadend = function () {
        setKey(reader.result);
        document.getElementById('key-modal').style.display = 'none';
    };
}

$(function () {
    $('#key-select').on('change', function () {
        var file = this.files[0];
        var reader = new FileReader();
        content = reader.readAsText(file);
        reader.onloadend = function () {
            setKey(reader.result);
            document.getElementById('key-modal').style.display = 'none';
        };

    });
});

function addHeader() {
    if ($('#header')[0] === undefined) {
        $('body').prepend('<div id="header" class="w3-bar header-bar">\n' +
                '    <div class="w3-dropdown-hover w3-right ">\n' +
                '        <button class="w3-button"><i class="material-icons material-icons-menu menu-item">language</i></button>\n' +
                '        <div class="w3-dropdown-content w3-bar-block w3-card-4">\n' +
                '            <a id="enItem" onclick="setLang(\'en\')" class="w3-bar-item w3-button"></a>\n' +
                '            <a id="faItem" onclick="setLang(\'fa\')" class="w3-bar-item w3-button"></a>\n' +
                '        </div>\n' +
                '    </div>\n' +
                '    <div class="w3-dropdown-hover w3-right ">\n' +
                '        <a href="/index.jsp"><button class="w3-button"><i class="material-icons material-icons-menu menu-item">home</i></button></a>\n' +
                '    </div>\n' +
                '</div>');
    }
}