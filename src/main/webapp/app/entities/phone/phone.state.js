(function() {
    'use strict';

    angular
        .module('finalApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('phone', {
            parent: 'entity',
            url: '/phone?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'finalApp.phone.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/phone/phones.html',
                    controller: 'PhoneController',
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
                    $translatePartialLoader.addPart('phone');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('phone-detail', {
            parent: 'entity',
            url: '/phone/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'finalApp.phone.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/phone/phone-detail.html',
                    controller: 'PhoneDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('phone');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Phone', function($stateParams, Phone) {
                    return Phone.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('phone.new', {
            parent: 'phone',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phone/phone-dialog.html',
                    controller: 'PhoneDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                number: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('phone', null, { reload: true });
                }, function() {
                    $state.go('phone');
                });
            }]
        })
        .state('phone.edit', {
            parent: 'phone',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phone/phone-dialog.html',
                    controller: 'PhoneDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Phone', function(Phone) {
                            return Phone.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('phone', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('phone.delete', {
            parent: 'phone',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/phone/phone-delete-dialog.html',
                    controller: 'PhoneDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Phone', function(Phone) {
                            return Phone.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('phone', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
