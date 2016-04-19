'use strict';

var app = angular.module('meyershop', []);


app.controller('home', [
		'$scope',
		'$http',
		'$q',
		function($scope, $http, $q) {

			$scope.menus = {};
			$scope.orders = {};
			$scope.days = [];
			
			$scope.increase = function(dayId, menuId) {
				var orderDetail = $scope.getOrderDetail(dayId, menuId);
				$scope.setOrderDetail(dayId, menuId, orderDetail + 1);
			};
			$scope.decrease = function(dayId, menuId) {
				var orderDetail = $scope.getOrderDetail(dayId, menuId);
				if (orderDetail > 0) {
					$scope.setOrderDetail(dayId, menuId, orderDetail - 1);
				}
			};
			
			$scope.getOrderDetail = function(dayId, menuId) {
				return $scope.orders[dayId].orderDetails[menuId];
			}
			$scope.setOrderDetail = function(dayId, menuId, value) {
				$scope.orders[dayId].orderDetails[menuId] = value;
			}
			
			$scope.saveData = function() {
				console.log($scope.orders);
				$('#loading').show();

				var savePromise = $http.post('orders/' + $scope.year + '/'
						+ $scope.week, $scope.orders);
				savePromise.then(function(e) {
					$('#loading').hide();
					console.log('Alles okay');
					console.log(e);
				}, function(e) {
					$('#loading').hide();
					console.log('Fehler beim Speichern');
					console.log(e);
				});
			};
			$scope.currentWeek = function() {
				var today = new Date();
				loadWeek(today.getMonday());
			}
			
			$scope.nextWeek = function() {
				loadWeek($scope.monday.addDays(7));
			};
			$scope.previousWeek = function() {
				loadWeek($scope.monday.addDays(-7));
			};
			$scope.currentWeek();
			
			/*private functions*/
			function loadWeek(monday){
				$scope.monday = monday;
				$scope.week = monday.getWeekNumber();
				$scope.year = monday.getFullYear();
				loadData();
			}
			
			function loadData() {
				$('#loading').show();
				$scope.menus = {};
				$scope.days = [];
				var menuPromise = $http.get('lecker/menu/' + $scope.year + '/'
						+ $scope.week), orderPromise = $http.get('orders/'
						+ $scope.year + '/' + $scope.week);
				$q.all([ menuPromise, orderPromise ]).then(function(data) {
					var menuData = data[0].data;
					var orderData = data[1].data;
					var days = menuData.map(function(e) {
						return e.date
					});
					days = days.filter(function(v, i) {
						return days.indexOf(v) == i;
					});
					var dayMenuMap = {};
					days.forEach(function(day) {
						dayMenuMap[day] = menuData.filter(function(v) {
							return v.date == day
						});
					});
					$scope.days = days;
					$scope.menus = dayMenuMap;
					$scope.orders = orderData;
					$('#loading').hide();
				});
			};

			
		} ]);