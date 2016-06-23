angular.module('kulya-pulya')
    .controller('loginController', function ($scope, $location, $http, $cookies, $rootScope, $window, constants) {
        console.log("In Login Controller");

        // Facebook onload hook
        $window.fbAsyncInit = function () {
            FB.init({
                appId: '549922185166119',
                cookie: true,
                status: true,
                xfbml: true,
                version: 'v2.4'
            });
        };

        // Facebook login method
        $scope.fbLogin = function () {
            FB.login(function (response) {
                if (response.status === 'connected') { // not_authorized, unknown
                    FB.api('/me', function (response) {
                        console.log(response.name + ' (id:' + response.id + ') logged into FACEBOOK');
                        $scope.login(response.name, "FACEBOOK", response.id);
                    });
                    //picture - GET /v2.5/{user-id}/picture HTTP/1.1 against Host: graph.facebook.com
                } else {
                }
            }, {scope: 'public_profile, email'});
        };

        //Google login method
        $scope.glLogin = function () {
            gapi.auth.authorize({
                client_id: constants.GOOGLE_CLIENT_ID,
                scope: 'https://www.googleapis.com/auth/plus.me'
            }, function(authResult) {
                if (authResult && !authResult.error) {
                    gapi.client.load('plus', 'v1', function () {
                        var request = gapi.client.plus.people.get({
                            userId: 'me'
                        });
                        request.execute(function (resp) {
                            //console.log('resp:' + JSON.stringify(resp));
                            console.log(resp.displayName + ' (id:' + resp.id + ') logged into GOOGLE');
                            $scope.login(resp.displayName, 'GOOGLE', resp.id);
                        });
                    });
                } else {
                    console.log('Google Login is not authorised. Response: ' + JSON.stringify(authResult));
                }
            });
        };

        $scope.login = function (name, socialNetwork, userId) {
            $http.post(
                'user/login',
                {name: name, socialNetwork: socialNetwork, socialNetworkId: userId}
            ).then(
                function (response) {
                    console.log("Logged in successfully: " + response);
                    var user = response.data;
                    if (user.sex == null) {
                        $location.path('/settings');
                    } else {
                        $location.path('/planner');
                    }
                    $cookies.put('user', JSON.stringify(user));
                    $rootScope.user = user;
                },
                function (response) {
                    console.error("Login problems. Status: " + response);
                }
            );
        }

    });