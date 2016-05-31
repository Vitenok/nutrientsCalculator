angular.module('kulya-pulya')
    .controller('googleFitController', function (ApplicationProperties, $scope, $location, $http, $cookies) {
        console.log("In Google Fit Controller");

        $scope.location = $location;

        var u = $cookies.get('user');
        if (u == undefined || u == null || u == '') {
            $location.path('/login');
            return;
        }
        $scope.user = JSON.parse(u);

        $scope.connectGoogleFit = function(){
            gapi.auth.authorize({
                client_id: '518156747499-cegh4ujuqfaq4v57ics5tlfvkor5h46j.apps.googleusercontent.com',
                scope: 'https://www.googleapis.com/auth/fitness.activity.read'
            }, function(authResult) {
                if (authResult && !authResult.error) {
                    $cookies.put('FitnessApplication', 'GoogleFit');
                } else {
                    console.log('Google Login is not authorised. Response: ' + JSON.stringify(authResult));
                }
            });
        };
    });