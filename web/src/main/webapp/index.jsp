<%@ page import="com.iti.foodCalculator.entity.Product" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html  data-ng-app="nutrientsCalc">
    <head>
        <title>Diet planner</title>
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
        <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">--%>
        <%--<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">--%>

        <%-- Themes from https://bootswatch.com/--%>
        <link rel="stylesheet" href="https://bootswatch.com/paper/bootstrap.min.css">
        <%--<link rel="stylesheet" href="https://bootswatch.com/cosmo/bootstrap.min.css">--%>

        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/angularjs/1.3.14/angular-cookies.min.js"></script>

        <%-- Custom styles--%>
        <link rel="stylesheet" type="text/css" href="styles/app.css">

        <%-- Custom scripts--%>
        <script type="text/javascript" src="scripts/nutrientsCalculator.js"></script>
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
                    <ul class="nav navbar-nav">
                        <li class="active"><a href="#">Diet planner</a></li>
                        <li><a href="#">Prepare your dish</a></li>
                        <li><a href="#">Your daily calorie intake</a></li>
                    </ul>
                    <ul class="nav navbar-nav navbar-right">
                        <li><a href="#">About</a></li>
                        <li><a href="#">Useful info</a></li>
                        <li><a href="#">Log in</a></li>
                    </ul>
                </div>
            </div>
        </nav>

        <div data-ng-controller="nutrientsCalcCtrl">
            <div class="col-md-5" >
                <div>
                    <form class="form-horizontal" name="caloriesForm">
                        <div class="form-group" ng-class="{ 'has-error' : caloriesForm.caloriesInput.$invalid }">
                            <div class="col-md-12">
                                <input type="number" name="caloriesInput" class="form-control" placeholder="Type your daily calories intake" ng-model="intake" min="800" required>
                            </div>
                        </div>
                    </form>
                </div>

                <div class="alert alert-danger" ng-show="caloriesForm.caloriesInput.$error.required">
                   This is required field
                </div>

                <div class="alert alert-danger"  ng-show="caloriesForm.caloriesInput.$error.min">
                    This is unhealthy amount of calories. We set minimum to 800 cKal, but you probably shouldn't cut your calorie intake even that much.
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
                            <td class="col-md-11"><h5>You've selected:</h5></td>
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
                        <li class="list-group-item" ng-repeat="select in selectedArr"> {{select.itemName}}<span class="glyphicon glyphicon-trash pull-right " ng-click="removeThisItem(select)"></span></li>
                    </ul>
                </div>
<%--TODO: add supplements form--%>
                <%--<div ng-show="!isDisabledButton" class="panel panel-default">--%>
                    <%--<div class="panel-body">--%>
                        <%--<form class="form-horizontal">--%>
                            <%--<fieldset>--%>
                                <%--<legend>Fill this form if you're going to use supplements:</legend>--%>
                                <%--<div class="form-group">--%>
                                    <%--<label for="supplement-name" class="col-lg-3 control-label">Supplement's name</label>--%>
                                    <%--<div class="col-lg-9">--%>
                                        <%--<input type="text" class="form-control" id="supplement-name" placeholder="Type here supplement's name">--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                                <%--<div class="form-group">--%>
                                    <%--<label for="proteins" class="col-lg-3 control-label">Proteins:</label>--%>
                                    <%--<div class="col-lg-9">--%>
                                        <%--<input type="number" class="form-control" id="proteins" placeholder="Type amount of proteins per 1 portion in grams">--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                                <%--<div class="form-group">--%>
                                    <%--<label for="fats" class="col-lg-3 control-label">Fats:</label>--%>
                                    <%--<div class="col-lg-9">--%>
                                        <%--<input type="number" class="form-control" id="fats" placeholder="Type amount of fats per 1 portion in grams">--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                                <%--<div class="form-group">--%>
                                    <%--<label for="carbs" class="col-lg-3 control-label">Carbs:</label>--%>
                                    <%--<div class="col-lg-9">--%>
                                        <%--<input type="number" class="form-control" id="carbs" placeholder="Type amount of carbohydrates per 1 portion in grams">--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                                <%--<div class="form-group">--%>
                                    <%--<div class="col-lg-3"></div>--%>
                                    <%--<div class="col-lg-9">--%>
                                        <%--<button type="submit" class="btn btn-danger right">Add</button>--%>
                                    <%--</div>--%>
                                <%--</div>--%>
                            <%--</fieldset>--%>
                        <%--</form>--%>
                    <%--</div>--%>
                <%--</div>--%>

                <div>
                    <button type="button" class="btn btn-danger btn-lg centered" ng-class="{disabled: isDisabledButton}" ng-click="calculateMenu()" ng-disabled="isDisabledButton">
                        Calculate
                    </button>
                </div>
                <h5 ng-show="isClicked">Your menu looks like:</h5>
                <div ng-show="isClicked" class="panel panel-default">
                    <div class="panel-body">
                        <table class="table table-hover table-small-font">
                            <thead>
                                <th class="col-md-7">Name</th>
                                <th class="col-md-1">Amount</th>
                                <th class="col-md-1">Protein</th>
                                <th class="col-md-1">Fat</th>
                                <th class="col-md-1">Carb</th>
                                <th class="col-md-1">kCal</th>
                            </thead>
                            <tbody>
                                <tr data-ng-repeat="menuItem in menu">
                                    <td class="col-md-7">{{menuItem.name}}: </td>
                                    <td  class="col-md-1"><b>{{menuItem.amount}} gr</b></td>
                                    <td  class="col-md-1">{{menuItem.totalProtein}} gr</td>
                                    <td  class="col-md-1">{{menuItem.totalFat}} gr</td>
                                    <td  class="col-md-1">{{menuItem.totalCarb}} gr</td>
                                    <td  class="col-md-1">{{menuItem.totalCalories}} kCal</td>
                                </tr>
                                <tr class=" bold" >
                                        <td class="col-md-7">Total:</td>
                                        <td class="col-md-1"></td>
                                        <td class="col-md-1"> {{totals.protein}} gr</td>
                                        <td class="col-md-1"> {{totals.fat}} gr</td>
                                        <td class="col-md-1">{{totals.carbs}} gr</td>
                                        <td class="col-md-1"> {{totals.kCal}} kCal</td>
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
