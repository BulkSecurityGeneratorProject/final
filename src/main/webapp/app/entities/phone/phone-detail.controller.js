(function() {
    'use strict';

    angular
        .module('finalApp')
        .controller('PhoneDetailController', PhoneDetailController);

    PhoneDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Phone', 'Student'];

    function PhoneDetailController($scope, $rootScope, $stateParams, entity, Phone, Student) {
        var vm = this;

        vm.phone = entity;

        var unsubscribe = $rootScope.$on('finalApp:phoneUpdate', function(event, result) {
            vm.phone = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
