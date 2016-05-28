angular
    .module('kulya-pulya', ['ui.slider', 'ngMaterial', 'ngMessages', 'ngRoute', 'ngCookies'])

    .constant("ApplicationProperties", {
        meals:'Breakfast,Lunch,Dinner,Snacks'
    })

    .config(['$routeProvider', function($routeProvider) {
        $routeProvider
            .when('/login', {templateUrl: '../pages/partials/login.html', controller: 'loginController'})
            .when('/logout', {templateUrl: '../pages/partials/login.html', controller: 'loginController'})
            .when('/settings', {templateUrl: '../pages/partials/settings.html', controller: 'settingsController'})
            .when('/planner', {templateUrl: '../pages/partials/planner.html', controller: 'plannerController'})
            .when('/registrar', {templateUrl: '../pages/partials/registrar.html', controller: 'registrarController'})
            .when('/user-products', {templateUrl: '../pages/partials/user-products.html', controller: 'userProductsController'})
            .otherwise({redirectTo: '/login'});
    }])

    .config(function($mdThemingProvider) {
        $mdThemingProvider.theme('default')
            .primaryPalette('indigo', {
            })
            .accentPalette('light-blue');
    })

    .run( function($rootScope, $location, $http, $cookies) {

        $rootScope.$on( '$routeChangeStart', function(event, next, current) {

            if (next.originalPath == '/login') {
                var u = $cookies.get('user');
                if (u != undefined && u != null && u != '') {
                    $location.path('/planner');
                    return;
                }
            }

            if (next.originalPath == '/logout') {
                $http.post('user/logout');
                $rootScope.user = null;
                $cookies.remove('user');
                $location.path('/login');
                return;
            }

        });
    })
    .controller('mainController', function ($scope, $location, $cookies, $rootScope) {
        $scope.location = $location;

        var u = $cookies.get('user');
        if (u == undefined || u == null || u == '') {
            $location.path('/login');
            return;
        }
        $rootScope.user = JSON.parse(u);

        console.log('In Main Controller');

        var originatorEv;

        $scope.openMenu = function($mdOpenMenu, ev) {
            originatorEv = ev;
            $mdOpenMenu(ev);
        };
    })
    ;
