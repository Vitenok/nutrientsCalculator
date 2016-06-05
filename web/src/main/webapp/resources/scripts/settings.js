angular.module('kulya-pulya')
    .controller('settingsController', function ($scope, $http, $timeout, $location, $cookies, constants) {
        console.log("In Settings Controller");

        $scope.location = $location;
        var u = $cookies.get('user');
        if (u == undefined || u == null || u == '') {
            $location.path('/login');
            return;
        }
        $scope.user = JSON.parse(u);

        $scope.user.isNew = false;
        if ($scope.user.sex == null) {
            $scope.user.activityLevel = 1.2;
            $scope.user.age = 25;
            $scope.user.carbohydratePercent = 40;
            $scope.user.fatPercent = 20;
            $scope.user.goal = 0;
            $scope.user.height = 175;
            $scope.user.proteinPercent = 40;
            $scope.user.sex = 'FEMALE';
            $scope.user.totalCalories = 2000;
            $scope.user.weight = 58;
            $scope.user.isNew = true;
        }


        $scope.allowTotalCaloriesUpdate = false;

        $scope.sex = {
            chosenSex: $scope.user.sex.startsWith('M') ? 'Male' : 'Female',
            options: [ 'Male', 'Female' ]
        };

        $scope.activityLevel = {
            chosenLevel: {value:$scope.user.activityLevel},
            options: [
                {name: "< 1 hour exercise per week", value: 1.1},
                {name: "1-3 hours exercise per week", value: 1.2},
                {name: "4-6 hours exercise per week", value: 1.35},
                {name: "6+ hours exercise per week", value: 1.45}
            ]
        };

        $scope.goal = {
            chosenGoal: {value:$scope.user.goal},
            options: [
                {name: "Maintain", value: 0},
                {name: "Cut", value: -0.2},
                {name: "Gain", value: 0.1}
            ]
        };

        $scope.calculateBMR = function (sex, age, weight, height) {
            return 10 * weight + 6.25 * height - 5 * age - (sex.startsWith('F') ? 161 : 5);
        };

        $scope.calculateTDEE = function (BMR, activityCoefficient) {
            return BMR * activityCoefficient;
        };

        $scope.$watch('[sex.chosenSex, user.age, user.height, user.weight, activityLevel.chosenLevel, goal.chosenGoal]', function (newVal, oldVal) {
            if (newVal == oldVal) {
                return;
            }
            $scope.BMR = Math.round($scope.calculateBMR($scope.sex.chosenSex, $scope.user.age, $scope.user.weight, $scope.user.height));
            $scope.TDEE = Math.round($scope.calculateTDEE($scope.BMR, $scope.activityLevel.chosenLevel.value));

            if ($scope.allowTotalCaloriesUpdate) {
                $scope.user.totalCalories = Math.round($scope.TDEE * (1 + $scope.goal.chosenGoal.value));
            } else {
                $scope.allowTotalCaloriesUpdate = true;
            }
        }, true);

        $scope.position = {
            floor: 0,
            ceiling: 100,
            firstKnob: $scope.user.proteinPercent,
            secondKnob: $scope.user.proteinPercent  +  $scope.user.fatPercent
        };

        $scope.$watch('position.firstKnob', function (newVal, oldVal) {
            if (newVal != oldVal) {
                $scope.user.proteinPercent = $scope.position.firstKnob;
                $scope.user.fatPercent = $scope.position.ceiling - ($scope.user.proteinPercent + $scope.user.carbohydratePercent);
            }
        }, true);

        $scope.$watch('position.secondKnob', function (newVal, oldVal) {
            if (newVal != oldVal) {
                $scope.user.carbohydratePercent = $scope.position.ceiling - $scope.position.secondKnob;
                $scope.user.fatPercent = $scope.position.ceiling - ($scope.user.proteinPercent + $scope.user.carbohydratePercent);
            }
        }, true);

        $scope.percentToCalories = function(persent, k) {
            return Math.round($scope.user.totalCalories * persent / 100 / k);
        };

        $scope.save = function() {
            console.log('Saving user...');
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
            if ($scope.user.isNew) {
                $scope.location.path('/planner');
            }
        };

        $scope.connectGoogleFit = function() {
            gapi.auth.authorize({
                client_id: constants.GOOGLE_CLIENT_ID,
                scope: 'https://www.googleapis.com/auth/fitness.activity.read'
            }, function(authResult) {
                if (authResult && !authResult.error) {
                    $scope.user.monitoringDevice = 'GoogleFit';
                    $scope.save();
                } else {
                    console.log('Google Login is not authorised. Response: ' + JSON.stringify(authResult));
                }
            });
        };

    });