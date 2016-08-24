(function() {
    'use strict';

    angular
        .module('finalApp')
        .factory('BloggerSearch', BloggerSearch);

    BloggerSearch.$inject = ['$resource'];

    function BloggerSearch($resource) {
        var resourceUrl =  'api/_search/bloggers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
