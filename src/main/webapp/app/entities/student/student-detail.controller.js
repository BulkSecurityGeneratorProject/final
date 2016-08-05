(function() {
    'use strict';

    angular
        .module('finalApp')
        .controller('StudentDetailController', StudentDetailController);

    StudentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Student', 'Phone'];

    function StudentDetailController($scope, $rootScope, $stateParams, entity, Student, Phone) {
        var vm = this;

        vm.student = entity;

        var unsubscribe = $rootScope.$on('finalApp:studentUpdate', function(event, result) {
            vm.student = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
