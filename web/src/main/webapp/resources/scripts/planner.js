angular.module('kulya-pulya')
    .config(function($mdDateLocaleProvider) {
        $mdDateLocaleProvider.formatDate = function(d) {
            return d.getDate()+'.'+(d.getMonth()+1)+'.'+d.getFullYear();
        };
    })
    .controller('plannerController', function (ApplicationProperties, $scope, $rootScope, $http, $timeout, $location, $cookies) {
            console.log("In Planner Controller");

            $scope.location = $location;

            var u = $cookies.get('user');
            if (u == undefined || u == null || u == '') {
                $location.path('/login');
                return;
            }
            $scope.user = JSON.parse(u);

            var mealTypes = ApplicationProperties.meals.split(',');

            $scope.mealType = {
                chosenMealType: {name: mealTypes[0]},
                options: [],
                mealsNames : []
            };
            $scope.meals = [];

            mealTypes.forEach(function(option){
                $scope.mealType.options.push({name:option});
                $scope.meals.push([]);
                $scope.mealType.mealsNames.push(option);
            });

            var d = new Date();
            d.setHours(d.getHours() - d.getTimezoneOffset() / 60);
            $scope.currentDate = d;
            $scope.$watch('currentDate', function (newVal, oldVal) {
                if (newVal != oldVal) {
                    var d = new Date(newVal);
                    d.setHours(d.getHours() - d.getTimezoneOffset() / 60);
                    $scope.loadDayPlan(d);
                }
            }, true);

            $scope.loadProducts = function () {
                $http.post('main/products', $scope.user.id).then(
                    function (response) {
                        $scope.products = response.data;
                        console.log("FoodItems received successfully");
                    },
                    function (error) {
                        console.error("FoodItems not received successfully. Status:" + JSON.stringify(error));
                    });
            };
            $scope.loadProducts();

            $scope.loadDayPlan = function (date) {
                $http.post('dayPlan/get', {
                    date: date,
                    userId: $scope.user.id
                }).then(
                    function (response) {
                        $scope.currentServings = [];
                        $scope.meals.forEach(function(meal){meal.length = 0});
                        $scope.calculatedMeals = [];
                        if (response.data != '') {
                            $scope.calculatedMeals = response.data;
                            $scope.updateCalculatedMeals(true);
                        }
                        console.log("Day food plan received successfully");
                    },
                    function (error) {
                        console.error("Day food plan not received successfully. Status:" + JSON.stringify(error));
                    });
            };
            $scope.loadDayPlan($scope.currentDate);

            $scope.addProductToMeal = function(product, mealIdx) {
                var productIdx = $scope.meals[mealIdx].indexOf(product);
                if (productIdx == -1) {
                    $scope.meals[mealIdx].push(product);
                    if (product.type == 'SUPPLEMENT') {
                        var servings = $scope.user.servings.filter(function (userServing) {
                            return userServing.productId == product.id;
                        }, product);
                        if (servings.length != 0) {
                            product.serving = servings[0].weight;
                        }
                    }
                }
                $scope.searchFoodItem = undefined;
            };

            $scope.currentServings = [];
            $scope.servingChanged = function(product){
                if (product.type != 'SUPPLEMENT') {
                    return;
                }
                product.serving = product.serving;
                var servings = $scope.currentServings.filter(function (serving) {
                    return serving.productId == product.id;
                }, product);
                if (servings.length == 0) {
                    $scope.currentServings.push({productId:product.id, weight:product.serving});
                } else {
                    servings[0].weight = product.serving;
                }
            };

            $scope.addProductToChosenMeal = function(product){
                var mealIdx = $scope.mealType.mealsNames.indexOf($scope.mealType.chosenMealType.name);
                $scope.addProductToMeal(product, mealIdx);
            };

            $scope.removeProductFromMeal = function(product, mealIdx) {
                $scope.meals[mealIdx] = $scope.meals[mealIdx].filter(function(currProduct){
                    return currProduct.id != product.id;
                }, product);
                $scope.currentServings = $scope.currentServings.filter(function(serving){
                    return serving.productId != product.id;
                }, product);
                /**
                if ($scope.calculatedMeals.length != 0) {
                    $scope.calculatedMeals[mealIdx].productPlans = $scope.calculatedMeals[mealIdx].productPlans.filter(function(currProductPlan){
                        return currProductPlan.product.id != product.id;
                    }, product);
                }
                 **/
            };

            $scope.calculatedMeals = [];
            $scope.calculateMenu = function() {
                var d = new Date($scope.currentDate);
                d.setHours(d.getHours() - d.getTimezoneOffset() / 60);
                $http.post('dayPlan/create', {
                    date: d,
                    productsLists: $scope.meals,
                    servings: $scope.currentServings,
                    userId: $scope.user.id
                }).then(
                    function(response){
                        $scope.calculatedMeals = response.data.mealPlans;
                        $scope.updateCalculatedMeals(false);
                        $scope.user = response.data.user;
                        $cookies.put('user', JSON.stringify(response.data.user));
                        console.log('Calculation successful : ' + JSON.stringify(response.status));
                    },
                    function(error){
                        console.error(' Error while calculating: ' + JSON.stringify(error));
                    });
            };

            $scope.showResult = function() {
                for (var i=0; i<$scope.calculatedMeals.length; i++) {
                    if ($scope.calculatedMeals[i].productPlans.length != 0) {
                        return true;
                    }
                }
                return false;
            }

            $scope.updateCalculatedMeals = function(updateMeals) {
                for (var i=0; i<$scope.calculatedMeals.length; i++) {
                    $scope.calculatedMeals[i].productPlans.forEach(function(productPlan){
                        if (updateMeals) {
                            $scope.addProductToMeal(productPlan.product, i);
                        }
                        productPlan.distribution = $scope.getProductPlanDistribution(productPlan.product, productPlan.weight);
                    });
                }
            };

            $scope.getProductPlanDistribution = function(product, weight) {
                return 'Protein: ' + Math.round(product.protein*weight/100) + ' gr, ' +
                    'Fat: ' + Math.round(product.fat*weight/100) + ' gr, ' +
                    'Carbs: ' + Math.round(product.carbo*weight/100) + ' gr, ' +
                    'kCal: ' + Math.round(product.kCal*weight/100) + ' kCal';
            }

    })
;