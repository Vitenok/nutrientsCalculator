var app = angular.module('nutrientsCalc', ['ui.slider', 'ui.bootstrap']);

app.controller('nutrientsCalcCtrl', function ($scope, $http, $filter) {
    $scope.selected = {};
    $scope.supplementItems = [];
    $scope.isDisabledButton = true;
    $scope.intake = 1200;
    $scope.isValidCalorieInput = true;


//    TODO
    //Material

    $scope.myDate = new Date();
    $scope.minDate = new Date(
        $scope.myDate.getFullYear(),
            $scope.myDate.getMonth() - 2,
        $scope.myDate.getDate());
    $scope.maxDate = new Date(
        $scope.myDate.getFullYear(),
            $scope.myDate.getMonth() + 2,
        $scope.myDate.getDate());
    $scope.onlyWeekendsPredicate = function(date) {
        var day = date.getDay();
        return day === 0 || day === 6;
    };

//

    /*  TODO angular datepi
     $scope.format = 'dd-MMMM-yyyy';

     $scope.today = function() {
     $scope.dt = new Date();
     };
     $scope.today();

     $scope.open = function($event) {
     $event.preventDefault();
     $event.stopPropagation();

     $scope.opened = true;
     };*/

    // Fetch data
    $scope.getProductsDataFromServer = function () {
        $http({method: 'GET', url: 'populateFoodItems'}).
            success(function (data, status, headers, config) {
                $scope.products = data;
                console.log("FoodItems received successfully");
//                    $scope.categories = [];
//                    for (i = 0; i < $scope.products.length; i++) {
//                        $scope.categories.push($scope.products[i].category);
////                        console.log($scope.categories[i]);
//                    }
//
//                    $scope.categoriesTree = $scope.categories[0].parent.parent.children;
//
////                    $scope.categories1 = _.uniqBy($scope.categories, 'id');
////                    for (i = 0; i < $scope.categories1.length; i++) {
////                        console.log($scope.categories1[i]);
////                    }
            }).
            error(function (data, status, headers, config) {
                console.error("FoodItems not received successfully. Status:" + status);
            });
    };

    $scope.mealType = "breakfast";

    $scope.breakfastInput = [];
    $scope.lunchInput = [];
    $scope.dinnerInput = [];
    $scope.snakesInput = [];
    $scope.selectedArr = [];

    $scope.addProductToCurrentMeal = function (product) {
        if ($scope.mealType == "breakfast") {
            $scope.breakfastInput.push(product);
            $scope.breakfastInput = removeDuplicatesFromArray($scope.breakfastInput);
        }
        if ($scope.mealType == "lunch") {
            $scope.lunchInput.push(product);
            $scope.lunchInput = removeDuplicatesFromArray($scope.lunchInput);
        }
        if ($scope.mealType == "dinner") {
            $scope.dinnerInput.push(product);
            $scope.dinnerInput = removeDuplicatesFromArray($scope.dinnerInput);
        }
        if ($scope.mealType == "snacks") {
            $scope.snakesInput.push(product);
            $scope.snakesInput = removeDuplicatesFromArray($scope.snakesInput);
        }

        $scope.selectedArr.push(product);
        $scope.selectedArr = removeDuplicatesFromArray($scope.selectedArr);
    };

    function removeDuplicatesFromArray(a) {
        return a.reduce(function (p, c) {
            if (p.indexOf(c) < 0) p.push(c);
            return p;
        }, []);
    }

    // Filter checked elements
//    $scope.getSelected = function () {
//        $scope.selectedArr = $filter('filter')($scope.products, {checked: true});
//        if ($scope.selectedArr.length == 'undefined' || $scope.selectedArr.length == 0) {
//            $scope.isDisabledButton = true;
//        } else {
//            $scope.isDisabledButton = false;
//        }
//        return $scope.selectedArr;
//    };

    $scope.position = {
        floor: 0,
        ceiling: 100,
        firstKnob: 40,
        secondKnob: 60
    };

//    $scope.checkParentId = function(id){
//        $scope.currentParentId = id;
//    }

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

    $scope.removeThisItem = function (selected, arr) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i].itemName === selected.itemName) {
//                $scope.selectedArr[i]['checked'] = false;
                var removedObject =arr.splice(i, 1);
                removedObject = null;
                break;
            }
        }

        if ($scope.breakfastInput != undefined || $scope.lunchInput != undefined || $scope.dinnerInput != undefined || $scope.snakesInput != undefined) {
            if ($scope.breakfastInput.length > 0 || $scope.lunchInput.length >0 || $scope.dinnerInput.length > 0 || $scope.snakesInput.length > 0) {
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

    // Calculate calorie need
    $scope.sex = {
        chosenSex: "",
        options: [
            {name: "male", value: "male"},
            {name: "female", value: "female"}
        ]
    };

    $scope.activityLevel = {
        chosenLevel: "",
        options: [
            {name: "< 1 hour exercise per week", value: 1.1},
            {name: "1-3 hours exercise per week", value: 1.2},
            {name: "4-6 hours exercise per week", value: 1.35},
            {name: "6+ hours exercise per week", value: 1.45}
        ]
    };

    $scope.goal = {
        chosenGoal: "",
        options: [
            {name: "Maintain", value: 1},
            {name: "Cut", value: -0.2},
            {name: "Gain", value: 0.1}
        ]
    };

    // basal metabolic rate (BMR)
    $scope.calculateBMR = function (sex, age, weight, height) {
        if (sex === "female") {
            return $scope.BMR = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        if (sex === "male") {
            return $scope.BMR = 10 * weight + 6.25 * height - 5 * age + 5;
        }
    };

    // total daily energy expenditure (TDEE)
    $scope.calculateTDEE = function (BMR, activityCoefficient) {
        return $scope.TDEE = BMR * activityCoefficient;
    };

    $scope.$watch('persDataForm.$valid', function (validity) {
        $scope.validity = validity;
        $scope.$watch('[sex.chosenSex, age, userHeight, userWeight, activityLevel.chosenLevel, goal.chosenGoal]', function (newVal, oldVal) {
            if ($scope.validity) {
                $scope.BMR = Math.round($scope.calculateBMR($scope.sex.chosenSex.value, $scope.age, $scope.userWeight, $scope.userHeight));
                $scope.TDEE = Math.round($scope.calculateTDEE($scope.BMR, $scope.activityLevel.chosenLevel.value));

                if ($scope.goal.chosenGoal.name === "Cut") {
                    return $scope.intake = Math.round($scope.TDEE + $scope.goal.chosenGoal.value * $scope.TDEE);
                }
                if ($scope.goal.chosenGoal.name === "Gain") {
                    return $scope.intake = Math.round($scope.TDEE + $scope.goal.chosenGoal.value * $scope.TDEE);
                }
                if ($scope.goal.chosenGoal.name === "Maintain") {
                    return $scope.intake = Math.round($scope.TDEE);
                }
            }

        }, true);

    });

    $scope.startsWith = function (actual, expected) {
        var lowerStr = (actual + "").toLowerCase();
        return lowerStr.indexOf(expected.toLowerCase()) === 0;
    };

    $scope.birthDate = '2013-07-23';
    $scope.dateOptions = {};
});

//app.directive('datepickerPopup', function (dateFilter, datepickerPopupConfig) {
//    return {
//        restrict: 'A',
//        priority: 1,
//        require: 'ngModel',
//        link: function(scope, element, attr, ngModel) {
//            var dateFormat = attr.datepickerPopup || datepickerPopupConfig.datepickerPopup;
//            ngModel.$formatters.push(function (value) {
//                return dateFilter(value, dateFormat);
//            });
//        }
//    };
//});

//app.directive('customDatepicker',function($compile,$timeout){
//    return {
//        replace:true,
//        templateUrl:'custom-datepicker.html',
//        scope: {
//            ngModel: '=',
//            dateOptions: '@',
//            dateDisabled: '@',
//            opened: '=',
//            min: '@',
//            max: '@',
//            popup: '@',
//            options: '@',
//            name: '@',
//            id: '@'
//        },
//        link: function($scope, $element, $attrs, $controller){
//
//        }
//    };
//})