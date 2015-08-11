<%@ page import="com.iti.foodCalculator.entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html  data-ng-app="nutrientsCalc">
    <head>
        <title>Diet planner</title>
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">--%>
        <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">--%>

        <%-- Themes from https://bootswatch.com/--%>
        <link rel="stylesheet" href="https://bootswatch.com/yeti/bootstrap.min.css">
        <%--<link rel="stylesheet" href="https://bootswatch.com/cosmo/bootstrap.min.css">--%>

        <%-- Custom styles--%>
        <link rel="stylesheet" type="text/css" href="styles/test.css">

        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>
        <%-- Custom scripts--%>
        <script type="text/javascript" src="scripts/nutrientsCalculator.js"></script>

    </head>

    <body>
    <div class="container margin-top-10">
        <nav class="navbar navbar-inverse">
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
                                <input type="number" class="form-control" placeholder="Type your daily calories intake" ng-model="intake" min="800" required>
                            </div>
                        </div>
                    </form>
                </div>
                <div role="alert">
                    <span class="error" ng-show="intakeForm.input.$error.required">
                      Required!</span>
                    <span class="error" ng-show="intakeForm.input.$error.number">
                      Not valid number!</span>
                </div>
                <div>
                    <form class="form-horizontal">
                        <div class="form-group">
                            <div class="col-md-12">
                                <input type="text" class="form-control" placeholder="Search food item..." ng-model="search">
                            </div>
                        </div>
                    </form>
                </div>
                <div>
                    <div class="">...or add it from the list:</div>
                </div>
                <div class="" data-ng-init="getProductsDataFromServer()">

                    <table class="table table-hover  table-fixed table-small-font">
                        <thead>
                        <th  class="col-md-3">Product name</th>
                        <th  class="col-md-2">Proteins</th>
                        <th  class="col-md-2">Fats</th>
                        <th  class="col-md-2">Carbs</th>
                        <th  class="col-md-2">Calories</th>
                        <th  class="col-md-1"></th>
                        </thead>
                        <tbody>
                        <tr data-ng-repeat="product in products | filter:search">
                            <td class="col-md-3">{{product.itemName}}</td>
                            <td class="col-md-2">{{product.proteins}}</td>
                            <td class="col-md-2">{{product.fats}}</td>
                            <td class="col-md-2">{{product.carbs}}</td>
                            <td class="col-md-2">{{product.kCal}}</td>
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
                <table >
                    <tbody>
                        <tr class="full-width">
                            <td class="col-md-11"><h4>You've selected:</h4></td>
                            <td class="col-md-1">
                                <button type="button" class="btn btn-default btn-sm" ng-show="!isDisabledButton" ng-click="clearSelection()">Clear all</button>
                            </td>
                        </tr>
                    </tbody>
                </table>

                <div class="well">
                    <div ng-show="isDisabledButton">
                        You didn't select anything yet
                    </div>
                    <ul  class="list-group">
                        <li class="list-group-item" ng-repeat="select in selectedArr"> {{select.itemName}}<span class="glyphicon glyphicon-trash pull-right" aria-hidden="true" ng-click="removeThisItem()"></span></li>
                    </ul>
                </div>

                <div>
                    <button type="button" class="btn btn-primary btn-lg centered" ng-class="{disabled: isDisabledButton}" ng-click="calculateMenu()">
                        Calculate
                    </button>
                </div>
                <div ng-show="isClicked">
                    <h4>Your menu looks like:</h4>
                    <div class="well">
                        <table class="table table-hover table-small-font">
                            <thead>
                                <th class="col-md-8">Name</th>
                                <th class="col-md-4">Amount</th>
                            </thead>
                            <tbody>
                            <tr data-ng-repeat="menuItem in menu">
                                <td class="col-md-8">{{menuItem.name}}: </td>
                                <td  class="col-md-4">{{menuItem.amount}} gr</td>
                            </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
    </body>
</html>
