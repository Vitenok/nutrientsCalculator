window.fbAsyncInit = function () {
    FB.init({
        appId: '549922185166119',
        cookie: true,
        status: true,
        xfbml: true,
        version: 'v2.5'
    });
    fbCheckLoginState();
};

function fbCheckLoginState() {
    FB.getLoginStatus(function (response) {
        console.log(response);
        if (response.status === 'connected') {
            FB.api('/me', function (response) {
                console.log(response.name + ' (id:' + response.id + ') logged in');
                var req = new XMLHttpRequest();
                req.open("GET", "login?name="+response.name+"&socialNetwork=FACEBOOK&token="+response.id);
                req.send();
                /**
                 * picture - GET /v2.5/{user-id}/picture HTTP/1.1 against Host: graph.facebook.com
                 */
            });
        }
    });
}

function fbLogin () {
    FB.login(function(response){
        if (response.status === 'connected') {
            // Logged into your app and Facebook.
            console.log("connected");
        } else if (response.status === 'not_authorized') {
            // The person is logged into Facebook, but not your app.
            console.log("not_authorized");
        } else {
            // The person is not logged into Facebook, so we're not sure if
            // they are logged into this app or not.
            console.log("unknown");
        }
        console.log(response);
    }, {scope: 'public_profile, email'});
}

function fbLogout () {
    var req = new XMLHttpRequest();
    req.open('GET', 'logout');
    req.send();
}