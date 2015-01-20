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
                    <th>Waiting</th>
                    <th>Target level</th>
                    <c:forEach items="${elevators}" var="elevator">
                        <th class="elevator">${elevator}</th>
                    </c:forEach>
                <tr>
            </thead>
            <tbody>
                <c:forEach var="i" begin="1" end="${numberOfLevels}" step="1">
                    <c:set var="level" value="${numberOfLevels - i}"/>
                    <c:set var="form" value="callForm${level}"/>
                    <tr>
                        <td>${level + 1}</td>
                        <td ng-bind="waiting[${level}]"></td>
                    	<td ng-form="${form}" ng-controller="FormController" class="form">
                            People: <input type="number" pattern="\d+" min="1" max="${maxRequest}" required="true" ng-model="data.passengers"/>
                            Target: <select ng-options="(a + 1) for a in [0, ${numberOfLevels - 1}, ${level}] | range" required="true" ng-model="data.to"></select>
                            <button ng-disabled="${form}.$invalid" ng-click="call(${level})">Call</button>
                        </td>
                    	<c:forEach items="${elevators}" var="elevator">
                            <td ng-bind-html="widgets['${elevator}'][${level}]"></td>
                        </c:forEach>
                </c:forEach>
            </tbody>
        </table>
        <script src="https://ajax.googleapis.com/ajax/libs/angularjs/1.3.8/angular.js"></script>
        <script>
            window.__settings = {
                numberOfLevels: ${numberOfLevels},
                elevatorNames: [<c:forEach items="${elevators}" var="elevator">'${elevator}',</c:forEach>]
            };
        </script>
        <script src="/scripts/app.js"></script>
    </body>
</html>