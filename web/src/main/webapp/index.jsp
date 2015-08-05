<%@ page import="com.iti.foodCalculator.entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html  ng-app="nutrientsCalc">
    <head>
        <title>Diet planner</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <!-- Latest compiled and minified CSS -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

        <!-- Optional theme -->
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
        <!-- Custom style -->
        <link rel="stylesheet" href="styles/app.css">

        <!-- Latest compiled and minified JavaScript -->
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
        <meta name="viewport" content="width=device-width, initial-scale=1">

        <%-- Angular scripts --%>
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>

        <%-- Custom script --%>
        <%--<script type="text/javascript" src="scripts/nutrientsCalculator.js"></script>--%>
        <%--<script type="text/javascript" src="scripts/nutrientsCalcCtrl.js"></script>--%>
    </head>

    <body>
        <div>

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

                <div>
                <div class="col-md-5" ng-controller="nutrientsCalcCtrl">
                    <div>
                        <form class="form-horizontal">
                            <div class="form-group">
                                <div class="col-md-12">
                                    <input type="text" class="form-control" placeholder="Type your daily calories intake">
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
                    <div class="">
                        asa
                        <p ng-repeat='item in items'>
                            <label><input type='checkbox' ng-model='selected[item]' /> {{item}}</label>
                        </p>

                        <p>selected : {{selected}}</p>
                        <p>selected array: {{getSelected()}}</p>

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
                    <%
                        List<Product> productsList = (List<Product>) request.getAttribute("productsList");
                        if (productsList != null) {
                    %>
                        <%
                            for (Product prod : productsList) {
                        %>
                            <tr>
                                <td class="col-md-3"><%=prod.getItemName()%></td>
                                <td class="col-md-2"><%=prod.getProteins()%></td>
                                <td class="col-md-2"><%=prod.getFats()%></td>
                                <td class="col-md-2"><%=prod.getCarbs()%></td>
                                <td class="col-md-2"><%=prod.getkCal()%></td>
                                <td class="col-md-1">
                                    <div class="checkbox">
                                        <label>
                                            <input type="checkbox" ng-model="selected[<%=prod.getItemName()%>]" value="<%=prod.getItemName()%>">
                                        </label>
                                    </div>
                                </td>
                            </tr>
                        <%
                            }
                        %>
                    <%
                        }
                    %>
                        </tbody>
                    </table>
                    </div>
                </div>
                <div class="col-md-7">
                    <h4>You've selected:</h4>
                    <div class="well">
                        {{getSelectedProducts()}}
                        <%--<ul>--%>
                            <%--<li>One</li>--%>
                            <%--<li>Two</li>--%>
                            <%--<li>Three</li>--%>
                        <%--</ul>--%>
                    </div>

                    <div>
                        <button type="button" class="btn btn-info btn-lg centered">
                            Calculate
                        </button>
                    </div>

                    <h4>Your menu looks like:</h4>
                    <div class="well">
                        <ul>
                            <li>One: 100gr</li>
                            <li>Two: 100gr</li>
                            <li>Three: 100gr</li>
                        </ul>
                    </div>
                </div>
        </div>
                    <script type="text/javascript">
                        var app = angular.module('nutrientsCalc', []);
                        app.controller('nutrientsCalcCtrl', function($scope){
//                            $scope.firstName = "john";
//                            $scope.lastName = "doe"
//                            $scope.selected = {};
//
//                            $scope.getSelectedProducts = function(){
//                                var selectedArr = [];
//
//                                angular.forEach($scope.selected, function(isSelected, val){
//                                    if(isSelected){
//                                        selectedArr.push(val);
//                                    }
//                                });
//                                return selectedArr;
//                            }


                            $scope.items = ['one', 'two', 'three'];
                            $scope.selected = {};

                            // Function to return selected items in array format
                            $scope.getSelected = function() {
                                var selectedArr = [];
                                angular.forEach($scope.selected, function(isSelected, val) {
                                    if (isSelected) {
                                        selectedArr.push(val);
                                    }
                                });
                                return selectedArr;
                            }


                        });
                    </script>
    </body>
</html>
