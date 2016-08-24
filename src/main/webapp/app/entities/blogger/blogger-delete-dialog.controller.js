(function() {
    'use strict';

    angular
        .module('finalApp')
        .controller('BloggerDeleteController',BloggerDeleteController);

    BloggerDeleteController.$inject = ['$uibModalInstance', 'entity', 'Blogger'];

    function BloggerDeleteController($uibModalInstance, entity, Blogger) {
        var vm = this;

        vm.blogger = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Blogger.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
