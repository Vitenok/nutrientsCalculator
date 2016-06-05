angular.module('kulya-pulya')
    .config(function($mdDateLocaleProvider) {
        $mdDateLocaleProvider.formatDate = function(d) {
            return d.getDate()+'.'+(d.getMonth()+1)+'.'+d.getFullYear();
        };
    })
    .controller('registrarController', function ($scope, $location, $http, $cookies, $timeout, constants) {
        console.log("In Registrar Controller");

        $scope.location = $location;

        var u = $cookies.get('user');
        if (u == undefined || u == null || u == '') {
            $location.path('/login');
            return;
        }
        $scope.user = JSON.parse(u);

        var mealTypes = constants.MEALS.split(',');
        $scope.mealType = {
            chosenMealType: {name: mealTypes[0]},
            options: [],
            mealsNames: []
        };
        mealTypes.forEach(function(option){
            $scope.mealType.options.push({name:option});
            $scope.mealType.mealsNames.push(option);
        });

        //var d = new Date();
        //d.setHours(d.getHours() - d.getTimezoneOffset() / 60);
        //$scope.currentDate = d;
        $scope.currentDate = new Date();
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
            console.log('Loading Day Plan for ' + date);
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
                    $scope.updateCaloriesDataForOneDay(date);
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
            $scope.caloriesDistChart.data[0][0] = $scope.stats.kC;
            $scope.caloriesDistChart.data[0][1] = $scope.stats.rkC;
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
            labels : ['Planned Calories From Meals', 'Registered Calories From Meals'],
            series : ['Calories'],
            data : [[]]
        };

        if ($scope.user.monitoringDevice != '') {
            $scope.caloriesDistChart.labels.push('Calories burned from activities');
            $scope.caloriesDistChart.labels.push('Calories burned from BMR');
        }

        $timeout($scope.updateCaloriesDataForOneDay, 1000);

        $scope.updateCaloriesDataForOneDay = function(fromDate){
            if ($scope.user.monitoringDevice == 'GoogleFit') {
                if (fromDate == undefined) {
                    fromDate = new Date();
                }
                fromDate.setHours(11);fromDate.setMinutes(0);fromDate.setSeconds(0);fromDate.setMilliseconds(0);
                var toDate = new Date(fromDate);toDate.setHours(0); toDate.setDate(toDate.getDate()+1);
                console.log('Updating calories from date ' + fromDate);
                console.log('Updating calories to date ' + toDate);
                $scope.updateCaloriesDataFromGoogleFit(fromDate.getTime(), toDate.getTime());
            }
        };

        $scope.updateCaloriesDataFromGoogleFit = function(fromDate, toDate) {
            if (typeof gapi == 'undefined') {
                console.error('gapi not loaded yet');
                return;
            }
            gapi.auth.authorize({
                client_id: constants.GOOGLE_CLIENT_ID,
                scope: 'https://www.googleapis.com/auth/fitness.activity.read',
                immediate: true
            }, function(authResult) {

                if (authResult.error) {
                    console.error('Google connection error: ' + authResult.error + ' : ' + authResult.error_subtype);
                    $scope.user.monitoringDevice = '';
                    $scope.caloriesDistChart.labels = ['Planned Calories From Meals', 'Registered Calories From Meals'];
                    $http.post('user/update',$scope.user).then(
                        function(response) {
                            console.log('User saved successfully');
                            $cookies.put('user', JSON.stringify(response.data));
                            $scope.user = response.data;
                        },
                        function(error) {
                            console.error('User not saved: ' + error);
                        }
                    );
                    return;
                }

                gapi.client.load('fitness', 'v1', function () {
                    var datasetId = fromDate * 1000000 + '-' + toDate * 1000000;
                    console.log('From date: ' + fromDate + ', to date: ' + toDate);
                    console.log('DatasetId:' + datasetId);

/*                    gapi.client.fitness.users.dataSources.datasets.get({
                        userId: 'me',
                        dataSourceId: 'derived:com.google.calories.expended:com.google.android.gms:from_activities',
                        datasetId: datasetId,
                        fields: 'point/value/fpVal'
                    }).execute(function (resp) {
                        if (resp.point == undefined) {return;}
                        $scope.$apply(function(){
                            var t = resp.point.reduce(
                                function(total, obj){
                                    //console.log('from_activities: ' + obj.value[0].fpVal);
                                    return total+parseFloat(obj.value[0].fpVal);
                                },
                                0);
                            $scope.caloriesFromActivities = Math.round(t);
                            $scope.caloriesDistChart.data[0][2] = $scope.caloriesFromActivities;
                            console.log('caloriesFromActivities: ' + $scope.caloriesFromActivities);
                        });
                    });

                    gapi.client.fitness.users.dataSources.datasets.get({
                        userId: 'me',
                        dataSourceId: 'derived:com.google.calories.expended:com.google.android.gms:from_bmr',
                        datasetId: datasetId,
                        fields: 'point/value/fpVal'
                    }).execute(function (resp) {
                        if (resp.point == undefined) {return;}
                        $scope.$apply(function(){
                            var t = resp.point.reduce(
                                function(total, obj){
                                    //console.log('from_bmr: ' + obj.value[0].fpVal);
                                    return total+parseFloat(obj.value[0].fpVal);
                                },
                                0);
                            $scope.caloriesFromBMR = Math.round(t);
                            $scope.caloriesDistChart.data[0][3] = $scope.caloriesFromBMR;
                            console.log('caloriesFromBMR: ' + $scope.caloriesFromBMR);
                        });
                    });*/


                    gapi.client.fitness.users.dataset.aggregate({
                        userId: 'me',
                        aggregateBy: [
                            {dataSourceId: 'derived:com.google.calories.expended:com.google.android.gms:from_activities'},
                            {dataSourceId: 'derived:com.google.calories.expended:com.google.android.gms:from_bmr'}
                            ],
                        endTimeMillis: toDate,
                        startTimeMillis: fromDate,
                        bucketByTime: {durationMillis: (toDate-fromDate)},
                        fields: 'bucket(dataset(point(originDataSourceId,value/fpVal)))'
                    }).execute(function (resp) {
                        if (resp.bucket == undefined) {return;}
                        $scope.$apply(function(){
                            resp.result.bucket.forEach(function(bucket){
                                var dataSourceId = bucket.dataset[0].point[0].originDataSourceId;
                                var val = Math.round(bucket.dataset[0].point[0].value[0].fpVal);
                                if (dataSourceId == 'derived:com.google.calories.expended:com.google.android.gms:from_activities') {
                                    $scope.caloriesDistChart.data[0][2] = val;
                                    console.log('Aggregated FROM_ACTIVITIES: ' + val);
                                } else if (dataSourceId == 'derived:com.google.calories.expended:com.google.android.gms:from_bmr') {
                                    $scope.caloriesDistChart.data[0][3] = val;
                                    console.log('Aggregated FROM_BMR: ' + val);
                                }
                            });
                        });
                    });


                });
            });
        };
    });