var app = angular.module('elevatorApp', []);

app.controller('TableController', ['$scope', '$http', '$interval', '$interpolate', '$sce',
    function($scope, $http, $interval, $interpolate, $sce) {

        /**
         * The elevators data arriving from the rest endpoint,
         * no need to keep it in scope as this is not watched.
         */
        var elevators = {};

        /**
         * The set of elevator widgets (little box with.
         */
        $scope.widgets = {};

        /**
         * Initialise the set of elevators to skip has own properties check
         * Initialise the set of widgets to be sure the target cell of specific
         * widget exists in the $scope widgets
         */
        (function() {
            var settings = window.__settings;
            var widgets = {};
            for (var i in settings.elevatorNames) {
                var elevatorName = settings.elevatorNames[i];
                elevators[elevatorName] = { level: 0 };
                widgets[elevatorName] = [];
                for (var level = 0; level < settings.numberOfLevels; level++) {
                    widgets[elevatorName][level] = '';
                }
            }
            $scope.widgets = widgets;
        }());

        /**
         *
         */
        $scope.waiting = {};

        /**
         * Get the latest feed, check if the elevators state as well as the waiting list updates have arrived.
         */
        var template = $interpolate('<span class="elevator-{{direction}}">{{passengers}}</span>');
        $interval(function() {
            $http.get('/service/feed').success(function(data, status, headers, config) {
                if (data.hasOwnProperty('elevators')) {
                    for (var id in data.elevators) {
                        // If here is a new data regarding the elevator[id]
                        if (!angular.equals(data.elevators[id], elevators[id])) {
                            // remove the old widget
                            $scope.widgets[id][elevators[id].level] = '';
                            // render the new widget
                            $scope.widgets[id][data.elevators[id].level] = $sce.trustAsHtml(template(data.elevators[id]));
                            // replace the old data with the new data
                            elevators[id] = data.elevators[id];
                        }
                    }
                }
                if (data.hasOwnProperty('waiting')) {
                    // Update the waiting list
                    $scope.waiting = data.waiting;
                }
            });
        }, 500);
    }
]);

app.controller('FormController', ['$scope', '$http',
    function($scope, $http) {

        /**
         * Call the elevator.
         */
        $scope.data = {};
        $scope.call = function(from) {
            $scope.data.from = from;
            $http.post('/service/call', $scope.data).success(function(data, status, headers, config) {
                $scope.data = {};
            });
        }
    }
]);

/**
 * Simple range filter to generate a list of numbers with one number skipped.
 */
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

/**
 * Disable caching in Internet Explorer
 */
app.config(['$httpProvider', function($httpProvider) {
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.get['If-Modified-Since'] = '0';
}]);