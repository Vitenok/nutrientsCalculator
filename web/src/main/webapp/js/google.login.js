function onSignIn(googleUser) {
    document.getElementById("gLogin").style.display = 'none';
    document.getElementById("gLogout").style.display = 'block';
    var profile = googleUser.getBasicProfile();
    console.log('ID: ' + profile.getId()); // Do not send to your backend! Use an ID token instead.
    console.log('Name: ' + profile.getName());
    console.log('Image URL: ' + profile.getImageUrl());
    console.log('Email: ' + profile.getEmail());
    /**
     * More - see here https://developers.google.com/identity/sign-in/web/sign-in
     */
}

function signOut() {
    document.getElementById("gLogin").style.display = 'block';
    document.getElementById("gLogout").style.display = 'none';
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
}