var app = angular.module('nutrientsCalc', []);

app.controller('nutrientsCalcCtrl', function($scope, $http, $filter){
    $scope.selected = {};
    $scope.isDisabledButton = true;
    $scope.intake = 1200;
    $scope.isValidCalorieInput = true;

    $scope.$watch('intake', function(newVal, oldVal){
        if (newVal === undefined || newVal == null || newVal < 800 ){
            $scope.isValidCalorieInput = false;
            $scope.isDisabledButton = true;
        } else {
            if ($scope.selectedArr.length > 0){
                $scope.isDisabledButton = false;
                $scope.isValidCalorieInput = true;
            }
        }
    }, true);

    $scope.$watch('selectedArr', function(newVal, oldVal){
        if ($scope.isValidCalorieInput && $scope.selectedArr.length > 0 ){
            $scope.isDisabledButton = false;
        } else {
            $scope.isDisabledButton = true;
        }
    }, true);

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
        var proteins = $scope.intake * 0.4 / 4;
        var carbs = $scope.intake * 0.4 / 4;
        var fats = $scope.intake * 0.2 / 9;

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
                        kcal: $scope.intake,
                        protein: proteins,
                        carb: carbs,
                        fat: fats
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
    };

    $scope.removeThisItem = function(selected){
        for (var i = 0; i < $scope.selectedArr.length; i++) {
            if ($scope.selectedArr[i].itemName === selected.itemName) {
                $scope.selectedArr[i]['checked'] = false;
                var removedObject = $scope.selectedArr.splice(i, 1);
                removedObject = null;
                break;
            }
        }

        if ($scope.selectedArr != undefined ){
            if ($scope.selectedArr.length > 0){
            }else {
                $scope.isDisabledButton = true;
                $scope.isClicked = false;
                $scope.menu = [];
            }
        }
    }
});