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
        $scope.newSupplement = {type:'SUPPLEMENT'};

        $scope.checkIfNameExists = function(name, element, isExistingProduct) {
            var existingProduct = $scope.user.products.filter(function (product) {
                return product.name == name;
            }, name);
            element.$setTouched(true);
            element.$setValidity('exists', isExistingProduct ? existingProduct.length == 1 : existingProduct.length == 0);
        }

        $scope.addProduct = function() {
            $scope.addProductForm.$setUntouched();
            $scope.user.products.unshift($scope.newProduct);
            $scope.newProduct = {};
            $scope.save();
        };

        $scope.addSupplement = function() {
            $scope.addSupplementForm.$setUntouched();
            $scope.user.products.unshift($scope.newSupplement);
            $scope.newSupplement = {type:'SUPPLEMENT'};
            $scope.save();
        }

        $scope.deleteProduct = function(product) {
            var i = $scope.user.products.indexOf(product);
            $scope.user.products.splice(i,1);
            $scope.save();
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