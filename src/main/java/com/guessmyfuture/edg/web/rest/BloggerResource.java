package com.guessmyfuture.edg.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.guessmyfuture.edg.domain.Blogger;
import com.guessmyfuture.edg.repository.BloggerRepository;
import com.guessmyfuture.edg.repository.search.BloggerSearchRepository;
import com.guessmyfuture.edg.web.rest.util.HeaderUtil;
import com.guessmyfuture.edg.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Blogger.
 */
@RestController
@RequestMapping("/api")
public class BloggerResource {

    private final Logger log = LoggerFactory.getLogger(BloggerResource.class);
        
    @Inject
    private BloggerRepository bloggerRepository;
    
    @Inject
    private BloggerSearchRepository bloggerSearchRepository;
    
    /**
     * POST  /bloggers : Create a new blogger.
     *
     * @param blogger the blogger to create
     * @return the ResponseEntity with status 201 (Created) and with body the new blogger, or with status 400 (Bad Request) if the blogger has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bloggers",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Blogger> createBlogger(@RequestBody Blogger blogger) throws URISyntaxException {
        log.debug("REST request to save Blogger : {}", blogger);
        if (blogger.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("blogger", "idexists", "A new blogger cannot already have an ID")).body(null);
        }
        Blogger result = bloggerRepository.save(blogger);
        bloggerSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/bloggers/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("blogger", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /bloggers : Updates an existing blogger.
     *
     * @param blogger the blogger to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated blogger,
     * or with status 400 (Bad Request) if the blogger is not valid,
     * or with status 500 (Internal Server Error) if the blogger couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/bloggers",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Blogger> updateBlogger(@RequestBody Blogger blogger) throws URISyntaxException {
        log.debug("REST request to update Blogger : {}", blogger);
        if (blogger.getId() == null) {
            return createBlogger(blogger);
        }
        Blogger result = bloggerRepository.save(blogger);
        bloggerSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("blogger", blogger.getId().toString()))
            .body(result);
    }

    /**
     * GET  /bloggers : get all the bloggers.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of bloggers in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/bloggers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Blogger>> getAllBloggers(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Bloggers");
        Page<Blogger> page = bloggerRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/bloggers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /bloggers/:id : get the "id" blogger.
     *
     * @param id the id of the blogger to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the blogger, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/bloggers/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Blogger> getBlogger(@PathVariable Long id) {
        log.debug("REST request to get Blogger : {}", id);
        Blogger blogger = bloggerRepository.findOne(id);
        return Optional.ofNullable(blogger)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /bloggers/:id : delete the "id" blogger.
     *
     * @param id the id of the blogger to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/bloggers/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteBlogger(@PathVariable Long id) {
        log.debug("REST request to delete Blogger : {}", id);
        bloggerRepository.delete(id);
        bloggerSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("blogger", id.toString())).build();
    }

    /**
     * SEARCH  /_search/bloggers?query=:query : search for the blogger corresponding
     * to the query.
     *
     * @param query the query of the blogger search
     * @return the result of the search
     */
    @RequestMapping(value = "/_search/bloggers",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Blogger>> searchBloggers(@RequestParam String query, Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to search for a page of Bloggers for query {}", query);
        Page<Blogger> page = bloggerSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/bloggers");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
