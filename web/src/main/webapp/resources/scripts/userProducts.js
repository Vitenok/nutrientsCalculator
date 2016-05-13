angular.module('kulya-pulya')
    .controller('userProductsController', function ($scope, $location, $http, $cookies) {
        console.log("In Custom Products Controller");

        $scope.location = $location;

        var u = $cookies.get('user');
        if (u == undefined || u == null || u == '') {
            $location.path('/login');
            return;
        }
        $scope.user = JSON.parse(u);
        $scope.newProduct = {};

        $scope.addProduct = function() {
            if ($scope.newProduct.serving != 0) {
                $scope.newProduct.type = 'SUPPLEMENT';
            }
            $scope.user.products.unshift($scope.newProduct);
            $scope.addProductNameForm.$setUntouched();
            $scope.addProductProteinForm.$setUntouched();
            $scope.addProductCarbohydrateForm.$setUntouched();
            $scope.addProductFatForm.$setUntouched();
            $scope.addProductCaloriesForm.$setUntouched();
            $scope.addProductServingForm.$setUntouched();
            $scope.newProduct = {};
            $scope.save();
        };

        $scope.deleteProduct = function(product) {
            var i = $scope.user.products.indexOf(product);
            $scope.user.products.splice(i,1);
        };

        $scope.save = function() {
            $http.post('user/products/save',
                {
                    products:$scope.user.products,
                    userId:$scope.user.id
                }).then(
                function(response){
                    console.log('Saved successfully ' + response.status);
                    $scope.user.products = response.data;
                    $cookies.put('user', JSON.stringify($scope.user));
                },
                function(response){
                    console.error('User products not updated properly: ' + JSON.stringify(response.data));
                });
        }
    });