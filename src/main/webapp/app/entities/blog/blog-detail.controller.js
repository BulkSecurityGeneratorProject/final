(function() {
    'use strict';

    angular
        .module('finalApp')
        .controller('BlogDetailController', BlogDetailController);

    BlogDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Blog'];

    function BlogDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Blog) {
        var vm = this;

        vm.blog = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('finalApp:blogUpdate', function(event, result) {
            vm.blog = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
