(function (d, s, id) {
    var js, fjs = d.getElementsByTagName(s)[0];
    if (d.getElementById(id)) return;
    js = d.createElement(s);
    js.id = id;
    js.src = "//connect.facebook.net/en_US/sdk.js";
    fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));
window.fbAsyncInit = function () {
    FB.init({
        appId: '549922185166119',
        cookie: true,
        status: true,
        xfbml: true,
        version: 'v2.5'
    });
};
function checkLoginState() {
    FB.getLoginStatus(function (response) {
        if (response.status === 'connected') {
            FB.api('/me', function (response) {
                console.log(response.name + ' (id:' + response.id + ') logged in');
                /**
                 * picture - GET /v2.5/{user-id}/picture HTTP/1.1 against Host: graph.facebook.com
                 */
            });
        }
    });
}