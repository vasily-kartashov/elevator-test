var app = angular.module('elevatorApp', []);

app.controller('TableController', ['$scope', '$http', '$interval', '$interpolate', '$sce',
    function($scope, $http, $interval, $interpolate, $sce) {

        $scope.widgets = {};
        $scope.waiting = {};

        var elevators = {};
        var template = $interpolate('<span class="elevator-{{direction}}">{{passengers}}</span>');
        $interval(function() {
            $http.get('/service/feed').success(function(data, status, headers, config) {
                if (data.hasOwnProperty('elevators')) {
                    for (var id in data.elevators) {
                        if (data.elevators.hasOwnProperty(id)) {
                            if (!elevators.hasOwnProperty(id) || !angular.equals(data.elevators[id], elevators[id])) {
                                elevators[id] = data.elevators[id];
                                var s = {};
                                s[elevators[id].level] = $sce.trustAsHtml(template(elevators[id]));
                                $scope.widgets[id] = s;
                            }
                        }
                    }
                }
                if (data.hasOwnProperty('waiting')) {
                    $scope.waiting = data.waiting;
                }
            });
        }, 500);
    }
]);

app.controller('FormController', ['$scope', '$http',
    function($scope, $http) {

        $scope.data = {};
        $scope.call = function(from) {
            $scope.data.from = from;
            $http.post('/service/call', $scope.data).success(function(data, status, headers, config) {
                $scope.data = {};
            });
        }
    }
]);

app.filter('range', function() {
    return function(args) {
        var result = [];
        for (var i = args[0]; i <= args[1]; i++) {
            if (i != args[2]) {
                result.push(i);
            }
        }
        return result;
    }
});
