'use strict';

var app = angular.module('meyershop', ['ngAnimate', 'ui.bootstrap']);
app.config(['$compileProvider', function ($compileProvider) {
  $compileProvider.debugInfoEnabled(false);
}]);

app.controller('HomeController', [
		'$scope',
		'$http',
		'$q',
		'dialogService',
		function($scope, $http, $q, dialogService) {
			var dirty = false;
			$scope.menus = {};
			$scope.orders = {};
			$scope.days = [];
			$scope.today = function(){
				return new Date().toSimpleString();
			}

			
			$scope.increase = function(dayId, menuId) {
				var orderDetail = $scope.getOrderDetail(dayId, menuId);
				$scope.setOrderDetail(dayId, menuId, orderDetail + 1);
				dirty = true;
			};
			$scope.decrease = function(dayId, menuId) {
				var orderDetail = $scope.getOrderDetail(dayId, menuId);
				if (orderDetail > 0) {
					$scope.setOrderDetail(dayId, menuId, orderDetail - 1);
					dirty = true;
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
					dirty = false;
					$('#loading').hide();
					dialogService.showInfoDialog('Ihre Bestellung wurde erfolgreich gespeichert', 'Erfolgreich')
				}, function(e) {
					$('#loading').hide();
					dialogService.showInfoDialog('Fehler beim Speichern Ihrer Bestellung', 'Fehler')
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
				if (dirty){
					var promise = dialogService.showConfirmDialog('Bestellungen wurden für diese Woche noch nicht gespeichert. Fortfahren?', 'Nicht gespeicherte Bestellungen'); 
					promise.then(function(result){
						if (result===true){
							loadData(monday);	
						}
					});
				} else  {
					loadData(monday);
				}
			}
			function loadData(monday) {
				$scope.monday = monday;
				$scope.week = monday.getWeekNumber();
				$scope.year = monday.getFullYear();
				$scope.month = monday.getMonth() +1;
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
					dirty = false;
					$('#loading').hide();
				});
			};

			
		} ]);

