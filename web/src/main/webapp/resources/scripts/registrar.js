angular.module('kulya-pulya')
    .controller('registrarController', function ($scope, $location, $http, $cookies) {
        console.log("In Registrar Controller");

        $scope.location = $location;

        var u = $cookies.get('user');
        if (u == undefined || u == null || u == '') {
            $location.path('/login');
            return;
        }
        $scope.user = JSON.parse(u);
    });