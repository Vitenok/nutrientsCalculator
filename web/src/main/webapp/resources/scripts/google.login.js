var auth2;

window.onLoadCallback = function() {
    glCheckLoginState();
};

function glCheckLoginState() {
    gapi.load('auth2', function() {
        auth2 = gapi.auth2.init({
            client_id: '518156747499-cegh4ujuqfaq4v57ics5tlfvkor5h46j.apps.googleusercontent.com'
        });
        auth2.isSignedIn.listen(signinChanged);
        auth2.currentUser.listen(userChanged);
        if (auth2.isSignedIn.get() == true) {
            auth2.signIn();
        }
        refreshValues();
    });
}

var signinChanged = function (val) {
    console.log('Google signin state changed to ', val);
};

var userChanged = function (user) {
    console.log('Google user now: ', user);
};

function refreshValues() {
    if (auth2) {
        console.log('Refreshing values...');
    }
}

function glLogin() {
    auth2.signIn().then(function(response){
        var profile = response.getBasicProfile();
        console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
        console.log('Name: ' + profile.getName());
        console.log('Image URL: ' + profile.getImageUrl());
        console.log('Email: ' + profile.getEmail());

        var req = new XMLHttpRequest();
        req.open("GET", "loginUser?name="+profile.getName()+"&socialNetwork=GOOGLE&token="+profile.getId());
        req.send();
    });
//document.getElementById("gLogin").style.display = 'none';
//document.getElementById("gLogout").style.display = 'block';

    /**
     * More - see here https://developers.google.com/identity/sign-in/web/sign-in
     */
}

function glSignOut() {
    document.getElementById("gLogin").style.display = 'block';
    document.getElementById("gLogout").style.display = 'none';
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
}