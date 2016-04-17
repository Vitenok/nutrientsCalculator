/*
//Facebook onload hook

window.fbAsyncInit = function () {
    FB.init({
        appId: '549922185166119',
        cookie: true,
        status: true,
        xfbml: true,
        version: 'v2.5'
    });
    getUserFromSession();
};

//Facebook login method

function fbLogin () {
    FB.login(function(response){
        if (response.status === 'connected') { // not_authorized, unknown
            FB.api('/me', function(response) {
                console.log(response.name + ' (id:' + response.id + ') logged into FACEBOOK');
                login(response.name, "FACEBOOK", response.id);
            });
            //picture - GET /v2.5/{user-id}/picture HTTP/1.1 against Host: graph.facebook.com
        }
    }, {scope: 'public_profile, email'});
}
*/
/**
//Google onload hook

var auth2;
function glCheckLoginState() {
    gapi.load('auth2', function() {
        auth2 = gapi.auth2.init({
            client_id: '518156747499-cegh4ujuqfaq4v57ics5tlfvkor5h46j.apps.googleusercontent.com'
        });
    });
}

//Google login method

function glLogin() {
    auth2.signIn().then(function(response){
        var profile = response.getBasicProfile();
        console.log(profile.getName() + ' (id:' + profile.getId() + ') logged into GOOGLE');
        login(profile.getName(), "GOOGLE", profile.getId());
    });
}
 **/

/*function getUserFromSession() {
    var req = new XMLHttpRequest();
    req.open("GET", "user", true);
    req.onload = function (e) {
        if (req.readyState == 4 && req.status == 200 && req.responseText != '') {
            var name = JSON.parse(req.responseText).name;
            afterLogin(name);
        } else {
            document.getElementById('loggedIn').style.display='none';
        }
        document.getElementById('signInBlock').style.display='';
    };
    req.send();
}*/

function login(name, socialNetwork, userId) {
    var req = new XMLHttpRequest();
    req.open("GET", "login?name="+name+"&socialNetwork="+socialNetwork+"&token="+userId, true);
    req.onload = function (e) {
        afterLogin(name);
    };
    req.onerror = function (e) {
        console.log(e);
    };
    req.send();
}

function afterLogin(name) {
    document.getElementById('loggedInTxt').innerHTML = name;
    document.getElementById('loggedIn').style.display='';
    document.getElementById('login').style.display='none';
}

/*
function logout () {
    var req = new XMLHttpRequest();
    req.open('GET', 'logout', true);
    req.onload = function (e) {
        document.getElementById('loggedInTxt').innerHTML = '';
        document.getElementById('loggedIn').style.display='none';
        document.getElementById('login').style.display='';
    };
    req.onerror = function (e) {
        console.log(e);
    };
    req.send();
}*/
