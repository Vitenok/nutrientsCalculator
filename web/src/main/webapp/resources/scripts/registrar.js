angular.module('kulya-pulya')
    .config(function($mdDateLocaleProvider) {
        $mdDateLocaleProvider.formatDate = function(d) {
            return d.getDate()+'.'+(d.getMonth()+1)+'.'+d.getFullYear();
        };
    })
    .controller('registrarController', function (ApplicationProperties, $scope, $location, $http, $cookies, $timeout) {
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

        $scope.stats = {
            p:0, c:0, f:0, kC:0,
            rp:0, rc:0, rf:0, rkC:0
        };

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
                        $scope.refreshChart();
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
                        $scope.refreshChart();
                    }
                    console.log("Day food plan received successfully");
                },
                function (error) {
                    console.error("Day food plan not received successfully. Status:" + JSON.stringify(error));
                });
        };

        $scope.registerNewWeight = function(productPlan) {
            $scope.refreshChart();
        };

        $scope.refreshChart = function() {
            $scope.stats = {
                p:0, c:0, f:0, kC:0,
                rp:0, rc:0, rf:0, rkC:0
            };
            $scope.calculatedMeals.forEach(function(calculatedMeal){
                calculatedMeal.productPlans.forEach(function(productPlan){
                    $scope.stats.p += Math.round(productPlan.weight*productPlan.product.protein/100);
                    $scope.stats.c += Math.round(productPlan.weight*productPlan.product.carbo/100);
                    $scope.stats.f += Math.round(productPlan.weight*productPlan.product.fat/100);
                    $scope.stats.rp += Math.round(productPlan.registeredWeight*productPlan.product.protein/100);
                    $scope.stats.rc += Math.round(productPlan.registeredWeight*productPlan.product.carbo/100);
                    $scope.stats.rf += Math.round(productPlan.registeredWeight*productPlan.product.fat/100);
                    var kCalPerGram = (productPlan.product.protein*4+productPlan.product.carbo*4+productPlan.product.fat*9)/100;
                    $scope.stats.kC += Math.round(productPlan.weight*kCalPerGram);
                    $scope.stats.rkC += Math.round(productPlan.registeredWeight*kCalPerGram);
                });
            });
            $scope.pcfChart.data = [
                [$scope.stats.p, $scope.stats.c, $scope.stats.f],
                [$scope.stats.rp, $scope.stats.rc, $scope.stats.rf]
            ];
        };

        $scope.pcfChart = {
            labels : ['Protein (g)', 'Carbohydrate (g)', 'Fat (g)'],
            series : ['Planned', 'Registered'],
            data : [
                [$scope.stats.p, $scope.stats.c, $scope.stats.f],
                [$scope.stats.rp, $scope.stats.rc, $scope.stats.rf]
            ]
        };

        $scope.caloriesDistChart = {
            labels : ['Calories burned from activities', 'Calories burned from BMR']
            //,data : [$scope.caloriesFromActivities, $scope.caloriesFromBMR]
        };

        $scope.fa = $cookies.get('FitnessApplication');
        //$scope.showStepsCount = false;
        //$scope.stepsCount = 0;

        $timeout($scope.getStepsCount, 1000);

        $scope.getStepsCount = function(){
            if ($scope.fa && $scope.fa == 'GoogleFit') {
                var date = new Date();
                date.setHours(0);date.setMinutes(0);date.setSeconds(0);
                $scope.getStepsCountFromGoogleFit(date.getTime(), new Date().getTime());
            }
        };

        $scope.getStepsCountFromGoogleFit = function(fromDate, toDate) {
            if (typeof gapi == 'undefined') {
                console.error('gapi not loaded yet');
                return;
            }
            gapi.auth.authorize({
                client_id: '518156747499-cegh4ujuqfaq4v57ics5tlfvkor5h46j.apps.googleusercontent.com',
                scope: 'https://www.googleapis.com/auth/fitness.activity.read',
                immediate: true
            }, function(authResult) {
                gapi.client.load('fitness', 'v1', function () {
                    gapi.client.fitness.users.dataSources.datasets.get({
                        userId: 'me',
                        dataSourceId: 'derived:com.google.calories.expended:com.google.android.gms:from_activities',
                        datasetId: (fromDate * 1000000 + '-' + toDate * 1000000),
                        fields: 'point/value/fpVal'
                    }).execute(function (resp) {
                        $scope.$apply(function(){
                            var t = resp.point.reduce(
                                function(total, obj){
                                    return total+parseFloat(obj.value[0].fpVal);
                                },
                                0);
                            $scope.caloriesFromActivities = Math.round(t);
                            //console.log('$scope.caloriesFromActivities: ' + $scope.caloriesFromActivities);
                        });
                    });

                    gapi.client.fitness.users.dataSources.datasets.get({
                        userId: 'me',
                        dataSourceId: 'derived:com.google.calories.expended:com.google.android.gms:from_bmr',
                        datasetId: (fromDate * 1000000 + '-' + toDate * 1000000),
                        fields: 'point/value/fpVal'
                    }).execute(function (resp) {
                        $scope.$apply(function(){
                            var t = resp.point.reduce(
                                function(total, obj){
                                    return total+parseFloat(obj.value[0].fpVal);
                                },
                                0);
                            $scope.caloriesFromBMR = Math.round(t);
                            //console.log('$scope.caloriesFromBMR: ' + $scope.caloriesFromBMR);
                        });
                    });
                });
            });
        }
    });