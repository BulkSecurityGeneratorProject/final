package com.guessmyfuture.edg.web.rest;

import com.guessmyfuture.edg.FinalApp;
import com.guessmyfuture.edg.domain.Blogger;
import com.guessmyfuture.edg.repository.BloggerRepository;
import com.guessmyfuture.edg.repository.search.BloggerSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the BloggerResource REST controller.
 *
 * @see BloggerResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = FinalApp.class)
@WebAppConfiguration
@IntegrationTest
public class BloggerResourceIntTest {

    private static final String DEFAULT_CONTENT = "AAAAA";
    private static final String UPDATED_CONTENT = "BBBBB";

    private static final String DEFAULT_BLOG = "AAAAA";
    private static final String UPDATED_BLOG = "BBBBB";

    @Inject
    private BloggerRepository bloggerRepository;

    @Inject
    private BloggerSearchRepository bloggerSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restBloggerMockMvc;

    private Blogger blogger;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BloggerResource bloggerResource = new BloggerResource();
        ReflectionTestUtils.setField(bloggerResource, "bloggerSearchRepository", bloggerSearchRepository);
        ReflectionTestUtils.setField(bloggerResource, "bloggerRepository", bloggerRepository);
        this.restBloggerMockMvc = MockMvcBuilders.standaloneSetup(bloggerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        bloggerSearchRepository.deleteAll();
        blogger = new Blogger();
        blogger.setContent(DEFAULT_CONTENT);
        blogger.setBlog(DEFAULT_BLOG);
    }

    @Test
    @Transactional
    public void createBlogger() throws Exception {
        int databaseSizeBeforeCreate = bloggerRepository.findAll().size();

        // Create the Blogger

        restBloggerMockMvc.perform(post("/api/bloggers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(blogger)))
                .andExpect(status().isCreated());

        // Validate the Blogger in the database
        List<Blogger> bloggers = bloggerRepository.findAll();
        assertThat(bloggers).hasSize(databaseSizeBeforeCreate + 1);
        Blogger testBlogger = bloggers.get(bloggers.size() - 1);
        assertThat(testBlogger.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testBlogger.getBlog()).isEqualTo(DEFAULT_BLOG);

        // Validate the Blogger in ElasticSearch
        Blogger bloggerEs = bloggerSearchRepository.findOne(testBlogger.getId());
        assertThat(bloggerEs).isEqualToComparingFieldByField(testBlogger);
    }

    @Test
    @Transactional
    public void getAllBloggers() throws Exception {
        // Initialize the database
        bloggerRepository.saveAndFlush(blogger);

        // Get all the bloggers
        restBloggerMockMvc.perform(get("/api/bloggers?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(blogger.getId().intValue())))
                .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
                .andExpect(jsonPath("$.[*].blog").value(hasItem(DEFAULT_BLOG.toString())));
    }

    @Test
    @Transactional
    public void getBlogger() throws Exception {
        // Initialize the database
        bloggerRepository.saveAndFlush(blogger);

        // Get the blogger
        restBloggerMockMvc.perform(get("/api/bloggers/{id}", blogger.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(blogger.getId().intValue()))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.blog").value(DEFAULT_BLOG.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBlogger() throws Exception {
        // Get the blogger
        restBloggerMockMvc.perform(get("/api/bloggers/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBlogger() throws Exception {
        // Initialize the database
        bloggerRepository.saveAndFlush(blogger);
        bloggerSearchRepository.save(blogger);
        int databaseSizeBeforeUpdate = bloggerRepository.findAll().size();

        // Update the blogger
        Blogger updatedBlogger = new Blogger();
        updatedBlogger.setId(blogger.getId());
        updatedBlogger.setContent(UPDATED_CONTENT);
        updatedBlogger.setBlog(UPDATED_BLOG);

        restBloggerMockMvc.perform(put("/api/bloggers")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedBlogger)))
                .andExpect(status().isOk());

        // Validate the Blogger in the database
        List<Blogger> bloggers = bloggerRepository.findAll();
        assertThat(bloggers).hasSize(databaseSizeBeforeUpdate);
        Blogger testBlogger = bloggers.get(bloggers.size() - 1);
        assertThat(testBlogger.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testBlogger.getBlog()).isEqualTo(UPDATED_BLOG);

        // Validate the Blogger in ElasticSearch
        Blogger bloggerEs = bloggerSearchRepository.findOne(testBlogger.getId());
        assertThat(bloggerEs).isEqualToComparingFieldByField(testBlogger);
    }

    @Test
    @Transactional
    public void deleteBlogger() throws Exception {
        // Initialize the database
        bloggerRepository.saveAndFlush(blogger);
        bloggerSearchRepository.save(blogger);
        int databaseSizeBeforeDelete = bloggerRepository.findAll().size();

        // Get the blogger
        restBloggerMockMvc.perform(delete("/api/bloggers/{id}", blogger.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate ElasticSearch is empty
        boolean bloggerExistsInEs = bloggerSearchRepository.exists(blogger.getId());
        assertThat(bloggerExistsInEs).isFalse();

        // Validate the database is empty
        List<Blogger> bloggers = bloggerRepository.findAll();
        assertThat(bloggers).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchBlogger() throws Exception {
        // Initialize the database
        bloggerRepository.saveAndFlush(blogger);
        bloggerSearchRepository.save(blogger);

        // Search the blogger
        restBloggerMockMvc.perform(get("/api/_search/bloggers?query=id:" + blogger.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blogger.getId().intValue())))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].blog").value(hasItem(DEFAULT_BLOG.toString())));
    }
}
