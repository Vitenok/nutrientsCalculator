var app = angular.module('nutrientsCalc', []);

app.controller('nutrientsCalcCtrl', function ($scope, $http, $filter) {
    $scope.selected = {};
    $scope.supplementItems = [];
    $scope.isDisabledButton = true;
    $scope.intake = 1200;
    $scope.isValidCalorieInput = true;

    $scope.$watch('intake', function (newVal, oldVal) {
        if (newVal === undefined || newVal == null || newVal < 800) {
            $scope.isValidCalorieInput = false;
            $scope.isDisabledButton = true;
        } else {
            if ($scope.selectedArr !== undefined && $scope.selectedArr.length > 0) {
                $scope.isDisabledButton = false;
                $scope.isValidCalorieInput = true;
            }
        }
    }, true);

    $scope.$watch('selectedArr', function (newVal, oldVal) {
        if ($scope.selectedArr !== undefined && $scope.isValidCalorieInput && $scope.selectedArr.length > 0) {
            $scope.isDisabledButton = false;
        } else {
            $scope.isDisabledButton = true;
        }
    }, true);

    // Fetch data
    $scope.getProductsDataFromServer = function () {
        $http({method: 'GET', url: 'populateFoodItems.web'}).
            success(function (data, status, headers, config) {
                $scope.products = data;
            }).
            error(function (data, status, headers, config) {
            });
    };

    // Filter checked elements
    $scope.getSelected = function () {
        $scope.selectedArr = $filter('filter')($scope.products, {checked: true});
        if ($scope.selectedArr.length == 'undefined' || $scope.selectedArr.length == 0) {
            $scope.isDisabledButton = true;
        } else {
            $scope.isDisabledButton = false;
        }
        return $scope.selectedArr;
    };

    // Calculate menu
    $scope.calculateMenu = function () {
        $scope.isClicked = false;
        var proteins = $scope.intake * 0.4 / 4;
        var carbs = $scope.intake * 0.4 / 4;
        var fats = $scope.intake * 0.2 / 9;


        var totalProtein = 0;
        var totalFat = 0;
        var totalCarb = 0;
        var totalCalories = 0;

        if ($scope.selectedArr !== undefined) {
            if ($scope.selectedArr.length > 0) {
                // Deep copy
                var selectedArrClone = jQuery.extend(true, [], $scope.selectedArr);

                selectedArrClone.forEach(function (entry) {
                    delete entry['$$hashKey'];
                    delete entry['checked'];
                });
                var dataJson = angular.toJson({
                    products: selectedArrClone,
                    supplementItems: $scope.supplementItems,
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
                    success(function (data, status, headers, config) {
                        $scope.menu = data;
                        $scope.isClicked = true;

                        // TODO: calculate totals per day in menu;
                        $scope.menu.forEach(function (entry) {
                            totalProtein += Number(entry.totalProtein);
                            totalFat += Number(entry.totalFat);
                            totalCarb += Number(entry.totalCarb);
                            totalCalories += Number(entry.totalCalories);
                        });

                        $scope.totals = {
                            protein: totalProtein,
                            fat: totalFat,
                            carbs: totalCarb,
                            kCal: totalCalories
                        }
                    }).
                    error(function (data, status, headers, config) {
                        $scope.isClicked = false;
                    });
            }
        }
    };

    $scope.clearSelection = function () {
        $scope.selectedArr.forEach(function (entry) {
            entry['checked'] = false;
        });
        $scope.selectedArr = [];
        $scope.isDisabledButton = true;
        $scope.isClicked = false;
        $scope.menu = [];
    };

    $scope.removeThisItem = function (selected) {
        for (var i = 0; i < $scope.selectedArr.length; i++) {
            if ($scope.selectedArr[i].itemName === selected.itemName) {
                $scope.selectedArr[i]['checked'] = false;
                var removedObject = $scope.selectedArr.splice(i, 1);
                removedObject = null;
                break;
            }
        }

        if ($scope.selectedArr != undefined) {
            if ($scope.selectedArr.length > 0) {
            } else {
                $scope.isDisabledButton = true;
                $scope.isClicked = false;
                $scope.menu = [];
            }
        }
    };

    $("#calculate-btn").click(function () {
        $('html, body').animate({
            scrollTop: $("#tf-clients").offset().top
        }, 2000);
    });

    $scope.addSupplementToTheList = function () {
        var w = $scope.sWeight / 100;
        $scope.sProteins = Math.round($scope.sProteins * w);
        $scope.sFats = Math.round($scope.sFats * w);
        $scope.sCarbs = Math.round($scope.sCarbs * w);

        $scope.sCalories = $scope.sProteins * 4 + $scope.sCarbs * 4 + $scope.sFats * 9;

        $scope.supplementItems.push({name: $scope.sName, kcal: $scope.sCalories, protein: $scope.sProteins, fat: $scope.sFats, carb: $scope.sCarbs, weight: $scope.sWeight});

        $scope.sName = '';
        $scope.sProteins = null;
        $scope.sFats = null;
        $scope.sCarbs = null;
        $scope.sCalories = null;
        $scope.sWeight = null;
    };

    $scope.clearAllSupplements = function () {
        $scope.supplementItems = [];
        $scope.suppl = false;
        $scope.supplement = {};
    };
});