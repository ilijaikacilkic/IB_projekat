'use strict';
// Declare app level module which depends on views, and components
angular.module('myApp', [
  'ngRoute',
  'myApp.contentPage',
  'myApp.login',
  'myApp.registration',
  'myApp.services'
]).
config(['$locationProvider', '$routeProvider', "$httpProvider", function($locationProvider, $routeProvider, $httpProvider) {
	$httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
  $routeProvider.otherwise({redirectTo: '/'});
}])
.controller('NavigationCtrl', ['$scope', '$rootScope', '$http', '$location', 'AuthService',
  function($scope, $rootScope, $http, $location, authService) {
    var self = this

    $rootScope.selectedTab = $location.path() || '/';
    
    $scope.downloadJKS = function() {
		var xhr = new XMLHttpRequest();
		xhr.open('GET', "/api/downloadJKS", true);
		xhr.setRequestHeader('Authorization', 'Bearer ' + authService.getJwtToken());
		xhr.responseType = 'blob';

		xhr.onload = function(e) {
			if (this.status == 200) {
				var blob = this.response;
				console.log(blob);
				var a = document.createElement('a');
				var url = window.URL.createObjectURL(blob);
				a.href = url;
				a.download = xhr.getResponseHeader('filename');
				a.click();
				window.URL.revokeObjectURL(url);
			}
		};

		xhr.send();
	}
    
    $scope.uploadZip = function(input) {
        var files = document.getElementById("uploadInput").files;
        if(files.length === 0) {
            alert("Please select a file");
        } else {
	        var formData = new FormData();
	        formData.append("file", files[0]);
	        
			var xhr = new XMLHttpRequest();
			xhr.open('POST', "/api/uploadZip", true);
			xhr.setRequestHeader('Authorization', 'Bearer ' + authService.getJwtToken());
			xhr.responseType = 'blob';
	
			xhr.onload = function(e) {
				if (this.status == 200) {
					alert("Uspesno ste uploadovali zip fajl");
				}
			};
	
			xhr.send(formData);
        }
	}

    $scope.logout = function() {
      authService.removeJwtToken();
      $rootScope.authenticated = false;
      $location.path("#/");
      $rootScope.selectedTab = "/";
    }

    $scope.setSelectedTab = function(tab) {
      $rootScope.selectedTab = tab;
    }

    $scope.tabClass = function(tab) {
      if ($rootScope.selectedTab == tab) {
        return "active";
      } else {
        return "";
      }
    }
    
    if ($rootScope.authenticated) {
      $location.path('/');
      $rootScope.selectedTab = '/';
      return;
    }
  }
]);