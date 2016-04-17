'use strict';

var app = angular.module('meyershop', []);

Date.prototype.getWeekNumber = function(){
    var d = new Date(+this);
    d.setHours(0,0,0);
    d.setDate(d.getDate()+4-(d.getDay()||7));
    return Math.ceil((((d-new Date(d.getFullYear(),0,1))/8.64e7)+1)/7);
};

app.controller('home', ['$scope','$http','$q', function($scope, $http, $q) {

	$scope.menus = {};
	$scope.orders = {};
	$scope.days = [];
	
	 $scope.loadData = function(){
		 $('#loading').show();
		 var menuPromise = $http.get('lecker/menu/' + $scope.year + '/' + $scope.week),
		    orderPromise = $http.get('orders/' + $scope.year + '/' + $scope.week);
		$scope.menus = {};
		$scope.days = [];
		$q.all([menuPromise, orderPromise]).then(
				function(data){
					var menuData = data[0].data;
					var orderData = data[1].data;
					var days = menuData.map(function(e){ return e.date});
			    	days = days.filter(function(v,i){return days.indexOf(v) == i;});
			    	var dayMenuMap = {};
			    	days.forEach(function(day){
			    		dayMenuMap[day] = menuData.filter(function (v) {return v.date == day});
			    	});
			    	$scope.days = days;
			    	$scope.menus = dayMenuMap;
			    	$scope.orders = orderData;
			    	$('#loading').hide(); 
				}
		);
	};
	$scope.saveData = function(){
		console.log($scope.orders);
		var savePromise = $http.post('orders/' + $scope.year + '/' + $scope.week, $scope.orders);
		savePromise.then(function (e){console.log("Done saving")});
	};
	$scope.currentWeek = function(){
		var today = new Date();
		$scope.week = today.getWeekNumber();
		$scope.year = today.getFullYear();
		$scope.loadData();
	}
	$scope.nextWeek = function(){
		 $scope.week++;
		 $scope.loadData();
	 };
	 $scope.previousWeek = function(){
		 $scope.week--;
		 $scope.loadData();
	 };
	 $scope.currentWeek();
	 
}]);