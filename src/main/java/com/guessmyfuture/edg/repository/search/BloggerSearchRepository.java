package com.guessmyfuture.edg.repository.search;

import com.guessmyfuture.edg.domain.Blogger;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Blogger entity.
 */
public interface BloggerSearchRepository extends ElasticsearchRepository<Blogger, Long> {
}
