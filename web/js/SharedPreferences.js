// global variable
var Lang;
var UserName;
var LoggedInTimeLimit = 30;

setLang();
setUserName();

function setLang() {
    Lang = localStorage.getItem('Lang');
    if (Lang === null) {
        localStorage.setItem('Lang', 'fa');
        Lang = 'fa';
    }
}

function setUserName() {
    if (localStorage.getItem('UserName') !== 'Guest') {
        var lasttime = Number(localStorage.getItem('LastTime'));
        if (Math.floor(Date.now() / 60000) - lasttime < LoggedInTimeLimit) {
            localStorage.setItem('LastTime', Math.floor(Date.now() / 60000));
            UserName = localStorage.getItem('UserName');
        } else {
            localStorage.setItem('UserName', 'Guest');
        }
    }
}





