var app = angular.module('elevatorApp', []);

app.controller('TableController', ['$scope', '$http', '$interval', '$interpolate', '$sce',
    function($scope, $http, $interval, $interpolate, $sce) {

        $scope.widgets = {};

        var elevators = {};
        $interval(function() {
            $http.get('/service/state').success(function(data, status, headers, config) {
                for (var id in data) {
                    if (!elevators.hasOwnProperty(id) || !angular.equals(data[id], elevators[id])) {
                        $scope.widgets[id] = updateWidgets(data[id], elevators[id]);
                        elevators[id] = data[id];
                    }
                }
            });
        }, 500);

        var template = $interpolate('<span class="elevator-{{direction}}">{{passengers}}</span>');
        var updateWidgets = function(after, before) {
            var scope = {};
            if (before && before.hasOwnProperty('level')) scope[before.level] = '';
            if (after) scope[after.level] = $sce.trustAsHtml(template(after));
            return scope;
        }
    }
]);

app.controller('FormController', ['$scope', '$http',
    function($scope, $http) {

        $scope.levels = function(start, end, skip) {
            var result = [];
            for (var i = start; i <= end; i++) {
                if (i != skip) {
                    result.push(i);
                }
            }
            return result;
        };

        $scope.data = {};
        $scope.call = function(from) {
            var data = {
                from: from,
                to: $scope.data.to,
                passengers: $scope.data.passengers
            }
            $http.post('/service/call', data).success(function(data, status, headers, config) {
                $scope.data = {};
            });
        }
    }
]);
