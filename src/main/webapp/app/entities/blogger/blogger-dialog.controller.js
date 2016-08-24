(function() {
    'use strict';

    angular
        .module('finalApp')
        .controller('BloggerDialogController', BloggerDialogController);

    BloggerDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'DataUtils', 'entity', 'Blogger'];

    function BloggerDialogController ($timeout, $scope, $stateParams, $uibModalInstance, DataUtils, entity, Blogger) {
        var vm = this;

        vm.blogger = entity;
        vm.clear = clear;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.blogger.id !== null) {
                Blogger.update(vm.blogger, onSaveSuccess, onSaveError);
            } else {
                Blogger.save(vm.blogger, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('finalApp:bloggerUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
