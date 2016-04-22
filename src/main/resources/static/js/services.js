'use strict';
app.service('dialogService', ['$uibModal', function($uibModal){
	this.showConfirmDialog = function(confirmText, confirmTitle){
		var modalInstance = $uibModal.open({
		      animation: true,
		      templateUrl: 'confirmDialog',
		      controller: 'ConfirmController',
		      resolve: {
		        content: function () {
		          return {title:confirmTitle, message: confirmText}
		        }
		      }
		    });
		    return modalInstance.result;
		 
	};
	this.showInfoDialog = function(infoText, infoTitle){
		 var modalInstance = $uibModal.open({
		      animation: true,
		      templateUrl: 'infoDialog',
		      controller: 'ConfirmController',
		      resolve: {
		        content: function () {
		          return {title:infoTitle, message: infoText}
		        }
		      }
		    });
	};

}]);
app.filter('numberFixedLen', function () {
    return function(a,b){
        return(1e4+""+a).slice(-b);
    };
});
app.controller('ConfirmController', function ($scope, $uibModalInstance, content) {
	  $scope.message = content.message;
	  $scope.title = content.title;
	  $scope.ok = function () {
	    $uibModalInstance.close(true);
	  };
	  $scope.cancel = function () {
	    $uibModalInstance.dismiss(false);
	  };
	});