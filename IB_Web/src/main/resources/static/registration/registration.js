angular.module('myApp.registration', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/registration', {
    templateUrl: 'registration/registration.html',
    controller: 'RegistrationCtrl'
  });
}])

.controller('RegistrationCtrl', ['$scope', '$rootScope', '$http', '$location', 'AuthService',
  function($scope, $rootScope, $http, $location, authService) {
  $scope.error = false;
  $rootScope.selectedTab = $location.path() || '/';

  $scope.credentials = {};

  $scope.registration = function() {
    // We are using formLogin in our backend, so here we need to serialize our form data
    $http({
      url: 'auth/registration',
      method: 'POST',
      data: $scope.credentials,
      headers: authService.createAuthorizationTokenHeader()
    })
    .success(function(res) {
      $rootScope.authenticated = true;
      authService.setJwtToken(res.access_token);
      $location.path("#/");
      $rootScope.selectedTab = "/";
      $scope.error = false;
    })
    .catch(function() {
      authService.removeJwtToken();
      $rootScope.authenticated = false;
      $scope.error = true;
    });
  };
}]);
