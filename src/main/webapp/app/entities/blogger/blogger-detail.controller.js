(function() {
    'use strict';

    angular
        .module('finalApp')
        .controller('BloggerDetailController', BloggerDetailController);

    BloggerDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'DataUtils', 'entity', 'Blogger'];

    function BloggerDetailController($scope, $rootScope, $stateParams, DataUtils, entity, Blogger) {
        var vm = this;

        vm.blogger = entity;
        vm.byteSize = DataUtils.byteSize;
        vm.openFile = DataUtils.openFile;

        var unsubscribe = $rootScope.$on('finalApp:bloggerUpdate', function(event, result) {
            vm.blogger = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
