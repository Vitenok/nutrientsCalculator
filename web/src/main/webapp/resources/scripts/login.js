angular.module('kulya-pulya')
    .controller('loginController', function ($scope, $location, $http, $cookies, $rootScope) {
        console.log("In Login Controller");

        // Facebook onload hook
        window.fbAsyncInit = function () {
            FB.init({
                appId: '549922185166119',
                cookie: true,
                status: true,
                xfbml: true,
                version: 'v2.5'
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
        $scope.auth2;
        $scope.glLogin = function () {
            if ($scope.auth2 == undefined) {
                gapi.load('auth2', function () {
                    $scope.auth2 = gapi.auth2.init({
                        client_id: '518156747499-cegh4ujuqfaq4v57ics5tlfvkor5h46j.apps.googleusercontent.com'
                    });
                    $scope.auth2.then(
                        function onInit() {
                            $scope.glLoginHook();
                        },
                        function onFailure() {
                            console.log("Connection with Google");
                        });
                });
            } else {
                console.log("Gapi loaded already");
                $scope.glLoginHook();
            }
        }

        $scope.glLoginHook = function () {
            $scope.auth2.signIn().then(function (response) {
                var profile = response.getBasicProfile();
                console.log(profile.getName() + ' (id:' + profile.getId() + ') logged into GOOGLE');
                $scope.login(profile.getName(), "GOOGLE", profile.getId());
            });
        }

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