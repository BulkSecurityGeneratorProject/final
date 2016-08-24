(function() {
    'use strict';
    angular
        .module('finalApp')
        .factory('Blogger', Blogger);

    Blogger.$inject = ['$resource'];

    function Blogger ($resource) {
        var resourceUrl =  'api/bloggers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
