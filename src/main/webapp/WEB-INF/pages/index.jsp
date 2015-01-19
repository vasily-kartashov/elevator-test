<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html lang="en" ng-app="elevatorApp">
    <head>
        <link rel="stylesheet" type="text/css" href="/stylesheets/main.css"/>
    </head>
    <body ng-controller="TableController">
        <table>
    	    <thead>
                <tr>
                    <th>Floor</th>
                    <th>Target level</th>
                    <c:forEach items="${elevatorPositions}" var="elevatorPosition">
                        <th class="elevator">${elevatorPosition.key}</th>
                    </c:forEach>
                <tr>
            </thead>
            <tbody>
                <c:forEach var="i" begin="1" end="${numberOfLevels}" step="1">
                    <c:set var="level" value="${numberOfLevels - i}"/>
                    <c:set var="form" value="callForm${level}"/>
                    <tr>
                        <td>${level + 1}</td>
                    	<td ng-form="${form}" ng-controller="FormController" class="form">
                            <input type="number" min="1" max="100" required="true" ng-model="data.passengers"/>
                            <select ng-options="(a + 1) for a in [0, ${numberOfLevels - 1}, ${level}] | range"
                                    required="true" ng-model="data.to"></select>
                            <button ng-disabled="${form}.$invalid" ng-click="call(${level})">Call</button>
                        </td>
                    	<c:forEach items="${elevatorPositions}" var="elevatorPosition">
                            <td ng-bind-html="widgets['${elevatorPosition.key}'][${level}]"></td>
                        </c:forEach>
                </c:forEach>
            </tbody>
        </table>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.8/angular.js"></script>
        <script src="/scripts/app.js"></script>
    </body>
</html>