var app = angular.module('nutrientsCalc', ['ui.slider']);

app.controller('nutrientsCalcCtrl', function ($scope, $http, $filter) {
    $scope.selected = {};
    $scope.supplementItems = [];
    $scope.isDisabledButton = true;
    $scope.intake = 1200;
    $scope.isValidCalorieInput = true;

    // Fetch data
    $scope.getProductsDataFromServer = function () {
        $http({method: 'GET', url: 'populateFoodItems'}).
            success(function (data, status, headers, config) {
                $scope.products = data;
                console.log("FoodItems received successfully");
            }).
            error(function (data, status, headers, config) {
                console.error("FoodItems not received successfully. Status:" + status);
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

    $scope.position = {
        floor: 0,
        ceiling: 100,
        firstKnob: 40,
        secondKnob: 60
    };

    $scope.proteinRange = $scope.position.firstKnob;
    $scope.carbohydrateRange = $scope.position.ceiling - $scope.position.secondKnob;
    $scope.fatRange = $scope.position.ceiling - ($scope.proteinRange + $scope.carbohydrateRange );

    $scope.transformCarbAndProteinFromPercentToGr = function (intake, range) {
        if (intake !== undefined && intake !== null) {
            return Math.round(intake * range / 100 / 4);
        } else {
            return 0;
        }
    };

    $scope.transformFatFromPercentToGr = function (intake, range) {
        if (intake !== undefined && intake !== null) {
            return Math.round(intake * range / 100 / 9);
        } else {
            return 0;
        }
    };


    $scope.proteinsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.proteinRange);
    $scope.fatsIntake = $scope.transformFatFromPercentToGr($scope.intake, $scope.fatRange);
    $scope.carbsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.carbohydrateRange);

    // Calculate menu
    $scope.calculateMenu = function () {
        $scope.isClicked = false;

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
                        protein: $scope.proteinsIntake,
                        carb: $scope.carbsIntake,
                        fat: $scope.fatsIntake
                    }
                });

                $http({
                    method: 'POST',
                    url: 'calculate',
                    data: dataJson,
                    headers: {
                        'Content-Type': 'application/json'
                    }
                }).
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

        $scope.supplementItems.push({
            name: $scope.sName,
            kcal: $scope.sCalories,
            protein: $scope.sProteins,
            fat: $scope.sFats,
            carb: $scope.sCarbs,
            weight: $scope.sWeight
        });

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

    $scope.$watch('intake', function (newVal, oldVal) {
        if (newVal !== undefined || newVal !== null) {
            if (newVal < 40) {
                $scope.isValidCalorieInput = false;
                $scope.isDisabledButton = true;

                $scope.showMinimumCalorieAlert = true;
            } else {
                $scope.showMinimumCalorieAlert = false;

                if ($scope.selectedArr !== undefined && $scope.selectedArr.length > 0) {
                    $scope.isDisabledButton = false;
                    $scope.isValidCalorieInput = true;
                }
            }
        }
        $scope.proteinsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.proteinRange);
        $scope.fatsIntake = $scope.transformFatFromPercentToGr($scope.intake, $scope.fatRange);
        $scope.carbsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.carbohydrateRange);
    }, true);

    $scope.$watch('position.firstKnob', function (newVal, oldVal) {
        if (newVal != oldVal) {
            $scope.proteinRange = $scope.position.firstKnob;
            $scope.fatRange = $scope.position.ceiling - ($scope.proteinRange + $scope.carbohydrateRange );

            $scope.proteinsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.proteinRange);
            $scope.fatsIntake = $scope.transformFatFromPercentToGr($scope.intake, $scope.fatRange);
        }
    }, true);

    $scope.$watch('position.secondKnob', function (newVal, oldVal) {
        if (newVal != oldVal) {
            $scope.carbohydrateRange = $scope.position.ceiling - $scope.position.secondKnob;
            $scope.fatRange = $scope.position.ceiling - ($scope.proteinRange + $scope.carbohydrateRange );

            $scope.carbsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.carbohydrateRange);
            $scope.fatsIntake = $scope.transformFatFromPercentToGr($scope.intake, $scope.fatRange);
        }
    }, true);

    $scope.$watch('selectedArr', function (newVal, oldVal) {
        if ($scope.selectedArr !== undefined && $scope.isValidCalorieInput && $scope.selectedArr.length > 0) {
            $scope.isDisabledButton = false;
        } else {
            $scope.isDisabledButton = true;
        }
    }, true);
});