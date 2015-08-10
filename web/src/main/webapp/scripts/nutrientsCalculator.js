var app = angular.module('nutrientsCalc', []);

app.controller('nutrientsCalcCtrl', function($scope, $http, $filter){
    $scope.selected = {};
    $scope.isDisabledButton = true;

    // Fetch data
    $scope.getProductsDataFromServer = function(){
        $http({method: 'GET', url: 'populateFoodItems.web'}).
            success(function(data, status, headers, config){
                $scope.products = data;
            }).
            error(function(data, status, headers, config){
            });
    };

    // Filter checked elements
    $scope.getSelected = function() {
        $scope.selectedArr = $filter('filter')($scope.products, {checked: true});
        if ($scope.selectedArr.length == 'undefined' || $scope.selectedArr.length == 0){
            $scope.isDisabledButton = true;
        } else {
            $scope.isDisabledButton = false;
        }
        return $scope.selectedArr;
    };

    // Calculate menu
    $scope.calculateMenu = function(){
        $scope.isClicked = false;

        if ($scope.selectedArr !== undefined){
            if ($scope.selectedArr.length > 0){
                // Deep copy
                var selectedArrClone = jQuery.extend(true, [], $scope.selectedArr);

                selectedArrClone.forEach(function(entry) {
                    delete entry['$$hashKey'];
                    delete entry['checked'];
                });
                var dataJson = JSON.stringify({
                    products: selectedArrClone,
                    supplementItems: [],
                    dailyMacroelementsInput: {
                        kcal: 0,
                        protein: 120,
                        carb: 120,
                        fat: 26.6
                    }
                });

                $http({
                    method: 'POST',
                    url: 'calculate.web',
                    data: dataJson,
                    headers: {
                        'Content-Type': 'application/json'
                    }}).
                    success(function(data, status, headers, config){
                        $scope.menu = data;
                        $scope.isClicked = true;
                    }).
                    error(function(data, status, headers, config){
                        $scope.isClicked = false;
                    });
            }
        }
    };

    $scope.clearSelection = function(){
        $scope.selectedArr.forEach(function(entry) {
            entry['checked'] = false;
        });
        $scope.selectedArr = [];
        $scope.isDisabledButton = true;
        $scope.isClicked = false;
        $scope.menu = [];
    }
});