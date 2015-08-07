<%@ page import="com.iti.foodCalculator.entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html  data-ng-app="nutrientsCalc">
    <head>
        <title>Diet planner</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
        <link rel="stylesheet" type="text/css" href="styles/app.css">
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>
        <%--<script type="text/javascript" src="scripts/nutrientsCalculator.js"></script>--%>
        <%--<script type="text/javascript" src="scripts/nutrientsCalcCtrl.js"></script>--%>

        <style type="text/css">
            .margin-bottom-20 {
                margin-bottom: 20px !important;
            }

            .margin-top-10{
                margin-top: 10px;
            }
            .centered {
                display: block;
                margin-right: auto;
                margin-left: auto;
            }
            .table-fixed{
                border: solid #E4D6D6 1px;
                height:  60%;
            }
            .table-fixed thead {
                width: 97%;
                font-size: 15px;
            }
            .table-fixed tbody {
                height:  90%;
                overflow-y: auto;
                width: 100%;
                font-size: 13px;
            }
            .table-fixed thead, .table-fixed tbody, .table-fixed tr, .table-fixed td, .table-fixed th {
                display: block;
            }
            .table-fixed tbody td, .table-fixed thead > tr> th {
                float: left;
                border-bottom-width: 0;
            }

        </style>
    </head>

    <body>
    <div class="container margin-top-10">
        <nav class="navbar navbar-default">
            <div class="container-fluid">

                <div class="navbar-header">
                    <a class="navbar-brand" href="#">
                        Logo
                        <%--<img alt="Brand" src="...">--%>
                    </a>
                </div>

                <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="#">About</a></li>
                        <li><a href="#">Useful info</a></li>
                        <li><a href="#">Log in</a></li>
                    </ul>
                </div>
            </div>
        </nav>
        <ul class="nav nav-tabs margin-bottom-20">
            <li role="presentation" class="active"><a href="#">Diet planner</a></li>
            <li role="presentation"><a href="#">Prepare your dish</a></li>
            <li role="presentation"><a href="#">Calculate macronutrients need</a></li>
        </ul>

        <div data-ng-controller="nutrientsCalcCtrl">
            <div class="col-md-5" >
                <div>
                    <form class="form-horizontal">
                        <div class="form-group">
                            <div class="col-md-12">
                                <input type="text" class="form-control" placeholder="Type your daily calories intake" ng-model="intake">
                            </div>
                        </div>
                    </form>
                </div>

                <div>
                    <form class="form-horizontal">
                        <div class="form-group">
                            <div class="col-md-12">
                                <input type="text" class="form-control" placeholder="Search food item...">
                            </div>
                        </div>
                    </form>
                </div>
                <div>
                    <div class="">...or add it from the list:</div>
                </div>
                <div class="" data-ng-init="getProductsDataFromServer()">

                    <table class="table table-hover  table-fixed">
                        <thead>
                        <th  class="col-md-3">Product name</th>
                        <th  class="col-md-2">Proteins</th>
                        <th  class="col-md-2">Fats</th>
                        <th  class="col-md-2">Carbs</th>
                        <th  class="col-md-2">Calories</th>
                        <th  class="col-md-1"></th>
                        </thead>
                        <tbody>
                        <tr data-ng-repeat="product in products">
                            <td class="col-md-3">{{product.itemName}}</td>
                            <td class="col-md-2">{{product.proteins}}</td>
                            <td class="col-md-2">{{product.fats}}</td>
                            <td class="col-md-2">{{product.kCal}}</td>
                            <td class="col-md-2">{{product.carbs}}</td>
                            <td class="col-md-1">
                                <div class="checkbox">
                                    <label>
                                        <input type="checkbox" ng-model="product.checked" ng-change="getSelected()">
                                    </label>
                                </div>
                            </td>
                        </tr>

                        </tbody>
                    </table>
                </div>
            </div>
            <div class="col-md-7">

                <h4>You've selected:</h4>
                <div class="well">
                    <div ng-show="isDisabledButton">
                        You didn't select anything yet
                    </div>
                    <ul ng-repeat="select in selectedArr">
                        <li>{{select.itemName}}</li>
                    </ul>
                </div>

                <div>
                    <button type="button" class="btn btn-info btn-lg centered " ng-class="{disabled: isDisabledButton}" ng-click="calculateMenu()">
                        Calculate
                    </button>
                </div>
                <h4>Your menu looks like:</h4>
                <div class="well">
                    {{menu}}

                    <ul>
                        <li>One: 100gr</li>
                        <li>Two: 100gr</li>
                        <li>Three: 100gr</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>

    <script type="text/javascript">
        var app = angular.module('nutrientsCalc', []);

        app.controller('nutrientsCalcCtrl', function($scope, $http, $filter){
            $scope.selected = {};
            $scope.isDisabledButton = true;

            // Fetch data
            $scope.getProductsDataFromServer = function(){
                $http({method: 'GET', url: 'populateFoodItems.web'}).
                        success(function(data, status, headers, config){
                            $scope.products = data;
                        }).
                        error(function(data, status, headers, config){
                        });
            };

            // Filter checked elements
            $scope.getSelected = function() {
                $scope.selectedArr = $filter('filter')($scope.products, {checked: true});
                if ($scope.selectedArr.length == 'undefined' || $scope.selectedArr.length == 0){
                    $scope.isDisabledButton = true;
                } else {
                    $scope.isDisabledButton = false;
                }
                return $scope.selectedArr;
            };

            // Calculate menu
            $scope.calculateMenu = function(){
                var dataJson = JSON.stringify({
                    products: $scope.selectedArr,
                    supplements: [],
                    distribution: {
                        kcal: 0,
                        protein: 120,
                        carb: 120,
                        fat: 26.6
                    }
                });

                $http({method: 'POST', url: 'calculate.web', data: dataJson}).
                        success(function(data, status, headers, config){
                            $scope.menu = data;
                        }).
                        error(function(data, status, headers, config){
                        });
            }
        });
    </script>
    </body>
</html>
