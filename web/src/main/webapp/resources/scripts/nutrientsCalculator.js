var app = angular.module('nutrientsCalc', ['ui.slider', 'ngMaterial', 'ngMessages', 'ngRoute']);

app.config(function($routeProvider) {
    $routeProvider

        // route for the settings page


        .when('/settings', {
            templateUrl : '../../pages/partials/userSettings.html',
            controller  : 'nutrientsCalcCtrl'
        })
;
});


app.controller('nutrientsCalcCtrl', function ($scope, $http, $timeout, $routeParams) {

    $routeParams.test = $scope.userData;


    $scope.userDataIsPresent = true;

    $scope.userData = {
        user: {
            sex: {name: "Male", value: "Male"},
            age: 25,
            height: 175,
            weight: 70,
            goal: {name: "Gain", value: 0.1},
            activityLvl: {name: "4-6 hours exercise per week", value: 1.35},
            savedCalories: 1500
        },
        macros: {
            protein: 38,
            fat: 33,
            carb: 29
        },
        menu: {
            meal1: [
                {name: "", weight: 0}
            ],
            meal2: [
                {name: "", weight: 0}
            ],
            meal3: [
                {name: "", weight: 0}
            ],
            meal4: [
                {name: "", weight: 0}
            ]
        }
    };

// Facebook onload hook
    window.fbAsyncInit = function () {
        FB.init({
            appId: '549922185166119',
            cookie: true,
            status: true,
            xfbml: true,
            version: 'v2.5'
        });
        getUserFromSession();
    };

// Facebook login method
    $scope.fbLogin = function(){
        FB.login(function (response) {
            if (response.status === 'connected') { // not_authorized, unknown
                FB.api('/me', function (response) {
                    console.log(response.name + ' (id:' + response.id + ') logged into FACEBOOK');
                    login(response.name, "FACEBOOK", response.id);
                });
                //picture - GET /v2.5/{user-id}/picture HTTP/1.1 against Host: graph.facebook.com
            } else {
            }
        }, {scope: 'public_profile, email'});
    };

    function getUserFromSession() {
        var req = new XMLHttpRequest();
        req.open("GET", "user", true);
        req.onload = function (e) {
            if (req.readyState == 4 && req.status == 200 && req.responseText != '') {
                var name = JSON.parse(req.responseText).name;
                afterLogin(name);
            } else {
                document.getElementById('loggedIn').style.display = 'none';
            }
            document.getElementById('signInBlock').style.display = '';
        };
        req.send();
    }

    function login(name, socialNetwork, userId) {
        var req = new XMLHttpRequest();
        req.open("GET", "login?name="+name+"&socialNetwork="+socialNetwork+"&token="+userId, true);
        req.onload = function (e) {
            afterLogin(name);
        };
        req.onerror = function (e) {
            console.log(e);
        };
        req.send();
    }

    function afterLogin(name) {
        /*
             TODO:call to check  saved user data on BE
             - if true: Show menu planner (not null response obj)
             - if false: Show Personal data input first (null response obj)
             Note: call should include date to return saved menu for current day
        */

        $scope.userDataIsPresent = true;
//        $scope.userIsSignedIn = true;

        if($scope.userDataIsPresent){
            // Data from response
            $scope.userData = {
                user: {
                    sex: {name: "Female", value: "Female"},
                    age: 30,
                    height: 158,
                    weight: 58,
                    goal: {name: "Cut", value: -0.2},
                    activityLvl: {name: "< 1 hour exercise per week", value: 1.1},
                    savedCalories: 1100
                },
                macros: {
                    protein: 38,
                    fat: 33,
                    carb: 29
                },
                menu: {
                    meal1: [
                        {name: "", weight: 0}
                    ],
                    meal2: [
                        {name: "", weight: 0}
                    ],
                    meal3: [
                        {name: "", weight: 0}
                    ],
                    meal4: [
                        {name: "", weight: 0}
                    ]
                }
            };

            $scope.sex.chosenSex = $scope.userData.user.sex;
            $scope.goal.chosenGoal = $scope.userData.user.goal;
            $scope.activityLevel.chosenLevel = $scope.userData.user.activityLvl;

            console.log($scope.sex.chosenSex);

            $scope.position.firstKnob = $scope.userData.macros.protein;
            $scope.position.secondKnob = $scope.userData.macros.protein  +  $scope.userData.macros.fat;
        } else {

        }


        // Toolbar buttons
        document.getElementById('loggedInTxt').innerHTML = name;
        document.getElementById('loggedIn').style.display='';
        document.getElementById('login').style.display='none';
    }

     $scope.logout = function() {
        var req = new XMLHttpRequest();
        req.open('GET', 'logout', true);
        req.onload = function (e) {
            document.getElementById('loggedInTxt').innerHTML = '';
            document.getElementById('loggedIn').style.display='none';
            document.getElementById('login').style.display='';
        };
        req.onerror = function (e) {
            console.log(e);
        };
        req.send();
    };

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
        chosenSex: $scope.userData.user.sex,
        options: [
            {name: "Male", value: "Male"},
            {name: "Female", value: "Female"}
        ]
    };

    $scope.activityLevel = {
        chosenLevel: $scope.userData.user.activityLvl,
        options: [
            {name: "< 1 hour exercise per week", value: 1.1},
            {name: "1-3 hours exercise per week", value: 1.2},
            {name: "4-6 hours exercise per week", value: 1.35},
            {name: "6+ hours exercise per week", value: 1.45}
        ]
    };

    $scope.goal = {
        chosenGoal: $scope.userData.user.goal,
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


    $scope.$watch('[sex.chosenSex, userData.user.age, userData.user.height, userData.user.weight, activityLevel.chosenLevel, goal.chosenGoal]', function (newVal, oldVal) {
        $scope.BMR = Math.round($scope.calculateBMR($scope.sex.chosenSex.value, $scope.userData.user.age, $scope.userData.user.weight, $scope.userData.user.height));
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

    $scope.$watch('intake', function (newVal, oldVal) {
        if (newVal != oldVal) {
            $scope.proteinsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.proteinRange);
            $scope.fatsIntake = $scope.transformFatFromPercentToGr($scope.intake, $scope.fatRange);
            $scope.carbsIntake = $scope.transformCarbAndProteinFromPercentToGr($scope.intake, $scope.carbohydrateRange);
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
    }, 200);

    $scope.round = function (i) {
        return Math.round(i);
    };
});

app.filter('findAllProducts', function() {
    return function(items, word) {
        var filtered = [];
        var startsWith = [];
        var contains = [];

        function compare(a,b) {
            if (a.name < b.name)
                return -1;
            else if (a.name > b.name)
                return 1;
            else
                return 0;
        }

        angular.forEach(items, function(item) {
            if (item !== undefined && word !== undefined){
                if(item.name.toLowerCase().indexOf(word.toLowerCase()) === 0){
                    startsWith.push(item);
                }
                if(item.name.toLowerCase().indexOf(word.toLowerCase()) > 0){
                    contains.push(item);
                }
            }
        });

        startsWith.sort(compare);
        contains.sort(compare);

        filtered = startsWith.concat(contains);
        return filtered;
    };
});