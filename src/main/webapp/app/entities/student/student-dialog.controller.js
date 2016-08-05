(function() {
    'use strict';

    angular
        .module('finalApp')
        .controller('StudentDialogController', StudentDialogController);

    StudentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Student', 'Phone'];

    function StudentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Student, Phone) {
        var vm = this;

        vm.student = entity;
        vm.clear = clear;
        vm.save = save;
        vm.phones = Phone.query();
        vm.addNewChoice=addNewChoice;
        vm.removeChoice=removeChoice;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.student.id !== null) {
                Student.update(vm.student, onSaveSuccess, onSaveError);
            } else {
                Student.save(vm.student, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('finalApp:studentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function addNewChoice () {
            var newItemNo = vm.student.phones.length+1;
            vm.student.phones.push({'id':'choice'+newItemNo});
        };
    
        function removeChoice = function() {
            var lastItem = vm.student.phones.length-1;
            vm.student.phones.splice(lastItem);
        };

    }
})();
