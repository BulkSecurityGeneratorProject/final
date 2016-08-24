(function() {
    'use strict';

    angular
        .module('finalApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('blogger', {
            parent: 'entity',
            url: '/blogger?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'finalApp.blogger.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/blogger/bloggers.html',
                    controller: 'BloggerController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('blogger');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('blogger-detail', {
            parent: 'entity',
            url: '/blogger/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'finalApp.blogger.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/blogger/blogger-detail.html',
                    controller: 'BloggerDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('blogger');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Blogger', function($stateParams, Blogger) {
                    return Blogger.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('blogger.new', {
            parent: 'blogger',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/blogger/blogger-dialog.html',
                    controller: 'BloggerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                content: null,
                                blog: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('blogger', null, { reload: true });
                }, function() {
                    $state.go('blogger');
                });
            }]
        })
        .state('blogger.edit', {
            parent: 'blogger',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/blogger/blogger-dialog.html',
                    controller: 'BloggerDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Blogger', function(Blogger) {
                            return Blogger.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('blogger', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('blogger.delete', {
            parent: 'blogger',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/blogger/blogger-delete-dialog.html',
                    controller: 'BloggerDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Blogger', function(Blogger) {
                            return Blogger.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('blogger', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
