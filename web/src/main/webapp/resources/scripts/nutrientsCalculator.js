var app = angular.module('nutrientsCalc', ['ui.slider', 'ngMaterial', 'ngMessages']);

app.controller('nutrientsCalcCtrl', function ($scope, $http, $timeout) {
    $scope.selected = {};
    $scope.supplementItems = [];
    $scope.isDisabledButton = true;
    $scope.intake = 1200;
    $scope.isValidCalorieInput = true;

    // Fetch data
    window.onload = function () {
        $scope.getProductsDataFromServer();
    };

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

    $scope.mealType = {
        chosenMealType: {name: "Breakfast"},
        options: [
            {name: "Breakfast"},
            {name: "Lunch"},
            {name: "Dinner"},
            {name: "Snacks"}
        ]
    };

    $scope.breakfastInput = [];
    $scope.lunchInput = [];
    $scope.dinnerInput = [];
    $scope.snakesInput = [];

    $scope.selectedArr = [];

    $scope.showBreakfast = false;
    $scope.showLunch = false;
    $scope.showDinner = false;
    $scope.showSnacks = false;

    $scope.addProductToCurrentMeal = function (product) {
        if ($scope.mealType.chosenMealType.name == "Breakfast") {
            $scope.toggle(product, $scope.breakfastInput);
            $scope.showBreakfast = $scope.breakfastInput.length > 0;
        }
        if ($scope.mealType.chosenMealType.name == "Lunch") {
            $scope.toggle(product, $scope.lunchInput);
            $scope.showLunch = $scope.lunchInput.length > 0;
        }
        if ($scope.mealType.chosenMealType.name == "Dinner") {
            $scope.toggle(product, $scope.dinnerInput);
            $scope.showDinner = $scope.dinnerInput.length > 0;
        }
        if ($scope.mealType.chosenMealType.name == "Snacks") {
            $scope.toggle(product, $scope.snakesInput);
            $scope.showSnacks = $scope.snakesInput.length > 0;
        }
        $scope.searchFoodItem = undefined;
        $scope.selectedArr.push(product);
        $scope.selectedArr = removeDuplicatesFromArray($scope.selectedArr);
    };

    $scope.toggle = function (item, list) {
        var idx = list.indexOf(item);
        if (idx > -1) list.splice(idx, 1);
        else list.push(item);
    };

    function removeDuplicatesFromArray(a) {
        return a.reduce(function (p, c) {
            if (p.indexOf(c) < 0) p.push(c);
            return p;
        }, []);
    }

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
            return intake * range / 100 / 4;
        } else {
            return 0;
        }
    };

    $scope.transformFatFromPercentToGr = function (intake, range) {
        if (intake !== undefined && intake !== null) {
            return intake * range / 100 / 9;
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

        function removeRedundandsFromClone(clone) {
            clone.forEach(function (entry) {
                delete entry['$$hashKey'];
                delete entry['checked'];
            });
            return clone;
        }

        if ($scope.selectedArr !== undefined) {
            if ($scope.selectedArr.length > 0) {
                // Deep copy
                var breakfastInputClone = removeRedundandsFromClone(jQuery.extend(true, [], $scope.breakfastInput));
                var lunchInputClone = removeRedundandsFromClone(jQuery.extend(true, [], $scope.lunchInput));
                var dinnerInputClone = removeRedundandsFromClone(jQuery.extend(true, [], $scope.dinnerInput));
                var snakesInputClone = removeRedundandsFromClone(jQuery.extend(true, [], $scope.snakesInput));

                var dataJson = angular.toJson({
                    productsLists: [breakfastInputClone, lunchInputClone, dinnerInputClone, snakesInputClone],
                    //todo
                    //supplementItems: $scope.supplementItems,
                    constrains: {
                        kCal: $scope.intake,
                        protein: $scope.proteinsIntake,
                        carbo: $scope.carbsIntake,
                        fat: $scope.fatsIntake
                    },
                    date: $scope.currentDate.toJSON()
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
                    }).
                    error(function (data, status, headers, config) {
                        $scope.isClicked = false;
                    });
            }
        }
    };

    //TODO
    $scope.clearSelection = function () {
        $scope.selectedArr.forEach(function (entry) {
            entry['checked'] = false;
        });
        $scope.selectedArr = [];
        $scope.isDisabledButton = true;
        $scope.isClicked = false;
        $scope.menu = [];
    };

    $scope.removeThisItem = function (item, list) {
        var idx = list.indexOf(item);
        if (idx > -1) list.splice(idx, 1);
    };

    // TODO
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

    // todo
    $scope.clearAllSupplements = function () {
        $scope.supplementItems = [];
        $scope.suppl = false;
        $scope.supplement = {};
    };

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

    $scope.$watch('[breakfastInput, lunchInput, dinnerInput, snakesInput]', function (newVal, oldVal) {
        if ($scope.breakfastInput.length == 0) {
            $scope.showBreakfast = false;
        }
        if ($scope.lunchInput.length == 0) {
            $scope.showLunch = false;
        }
        if ($scope.dinnerInput.length == 0) {
            $scope.showDinner = false;
        }
        if ($scope.snakesInput.length == 0) {
            $scope.showSnacks = false;
        }

        if ($scope.breakfastInput.length == 0 && $scope.lunchInput.length == 0 && $scope.dinnerInput.length == 0 && $scope.snakesInput.length == 0) {
            $scope.isDisabledButton = true;
        } else {
            $scope.isDisabledButton = false;
        }
    }, true);

    // Calculate calorie need
    $scope.sex = {
        chosenSex: {name: "Female", value: "Female"},
        options: [
            {name: "Male", value: "Male"},
            {name: "Female", value: "Female"}
        ]
    };

    $scope.activityLevel = {
        chosenLevel: {name: "1-3 hours exercise per week", value: 1.2},
        options: [
            {name: "< 1 hour exercise per week", value: 1.1},
            {name: "1-3 hours exercise per week", value: 1.2},
            {name: "4-6 hours exercise per week", value: 1.35},
            {name: "6+ hours exercise per week", value: 1.45}
        ]
    };

    $scope.goal = {
        chosenGoal: {name: "Cut", value: -0.2},
        options: [
            {name: "Maintain", value: 1},
            {name: "Cut", value: -0.2},
            {name: "Gain", value: 0.1}
        ]
    };

    // basal metabolic rate (BMR)
    $scope.calculateBMR = function (sex, age, weight, height) {
        if (sex === "Female") {
            return $scope.BMR = 10 * weight + 6.25 * height - 5 * age - 161;
        }
        if (sex === "Male") {
            return $scope.BMR = 10 * weight + 6.25 * height - 5 * age + 5;
        }
    };

    // total daily energy expenditure (TDEE)
    $scope.calculateTDEE = function (BMR, activityCoefficient) {
        return $scope.TDEE = BMR * activityCoefficient;
    };


    $scope.$watch('[sex.chosenSex, age, userHeight, userWeight, activityLevel.chosenLevel, goal.chosenGoal]', function (newVal, oldVal) {
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
    }, true);

    $scope.startsWith = function (actual, expected) {
        if (expected !== undefined && expected !== null && expected !== "") {
            var lowerStr = (actual + "").toLowerCase();
            return lowerStr.indexOf(expected.toLowerCase()) === 0;
        }
    };

    // Datepicker
    $scope.currentDate = new Date();
    $scope.minDate = new Date($scope.currentDate.getFullYear(), $scope.currentDate.getMonth(), $scope.currentDate.getDate());
    $scope.maxDate = new Date($scope.currentDate.getFullYear(), $scope.currentDate.getMonth() + 1, $scope.currentDate.getDate());
    console.log($scope.currentDate);

    // Timeout for redrawing of slider due to bug
    $timeout(function () {
        window.dispatchEvent(new Event('resize'));
    }, 500);

    $scope.round = function (i) {
        return Math.round(i);
    };
});
