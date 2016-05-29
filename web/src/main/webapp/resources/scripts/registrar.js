angular.module('kulya-pulya')
    .config(function($mdDateLocaleProvider) {
        $mdDateLocaleProvider.formatDate = function(d) {
            return d.getDate()+'.'+(d.getMonth()+1)+'.'+d.getFullYear();
        };
    })
    .controller('registrarController', function (ApplicationProperties, $scope, $location, $http, $cookies) {
        console.log("In Registrar Controller");

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
            mealsNames: []
        };
        mealTypes.forEach(function(option){
            $scope.mealType.options.push({name:option});
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

        $scope.calculatedMeals = [];
        $scope.loadDayPlan = function (date) {
            $http.post('dayPlan/get', {
                date: date,
                userId: $scope.user.id
            }).then(
                function (response) {
                    $scope.calculatedMeals = [];
                    if (response.data != '') {
                        $scope.calculatedMeals = response.data;
                        $scope.updateCalculatedMeals();
                    }
                    console.log("Day food plan received successfully");
                },
                function (error) {
                    console.error("Day food plan not received successfully. Status:" + JSON.stringify(error));
                });
        };
        $scope.loadDayPlan($scope.currentDate);

        $scope.updateCalculatedMeals = function() {
            $scope.calculatedMeals.forEach(function(calculatedMeal){
                calculatedMeal.productPlans.forEach(function(productPlan){
                    if (productPlan.registeredWeight == 0) {
                        productPlan.registeredWeight = productPlan.weight;
                    }
                    productPlan.distribution = $scope.getProductPlanDistribution(productPlan.product, productPlan.weight);
                });
            });
        };

        $scope.getProductPlanDistribution = function(product, weight) {
            return 'Protein: ' + Math.round(product.protein*weight/100) + ' gr, ' +
                'Fat: ' + Math.round(product.fat*weight/100) + ' gr, ' +
                'Carbs: ' + Math.round(product.carbo*weight/100) + ' gr, ' +
                'kCal: ' + Math.round(product.kCal*weight/100) + ' kCal';
        };

        $scope.addProductToChosenMeal = function(product){
            var mealIdx = $scope.mealType.mealsNames.indexOf($scope.mealType.chosenMealType.name);
            $scope.addProductToMeal(product, mealIdx);
        };

        $scope.addProductToMeal = function(product, mealIdx) {
            var productIdx = $scope.calculatedMeals[mealIdx].productPlans.filter(function(productPlan){
                return productPlan.product.id == product.id;
            }, product);
            if (productIdx.length == 0) {
                if (product.type == 'SUPPLEMENT') {
                    var servings = $scope.user.servings.filter(function (userServing) {
                        return userServing.productId == product.id;
                    }, product);
                    if (servings.length != 0) {
                        product.serving = servings[0].weight;
                    }
                }
                $scope.calculatedMeals[mealIdx].productPlans.push({
                    product: product,
                    weight: product.serving,
                    distribution: $scope.getProductPlanDistribution(product, product.serving)
                });
            }
            $scope.searchFoodItem = undefined;
        };

        $scope.updateDayPlan = function() {
            $http.post('dayPlan/register', {
                userId: $scope.user.id,
                meals: $scope.calculatedMeals
            }).then(
                function (response) {
                    $scope.calculatedMeals = [];
                    if (response.data != '') {
                        $scope.calculatedMeals = response.data;
                        $scope.updateCalculatedMeals();
                    }
                    console.log("Day food plan received successfully");
                },
                function (error) {
                    console.error("Day food plan not received successfully. Status:" + JSON.stringify(error));
                });
        }

        $scope.labels = ["January", "February", "March", "April", "May", "June", "July"];
        $scope.series = ['Series A', 'Series B'];
        $scope.chartdata = [
            [65, 59, 80, 81, 56, 55, 40],
            [28, 48, 40, 19, 86, 27, 90]
        ];
    });