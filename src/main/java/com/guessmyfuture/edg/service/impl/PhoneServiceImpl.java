package com.guessmyfuture.edg.service.impl;

import com.guessmyfuture.edg.service.PhoneService;
import com.guessmyfuture.edg.domain.Phone;
import com.guessmyfuture.edg.repository.PhoneRepository;
import com.guessmyfuture.edg.repository.search.PhoneSearchRepository;
import com.guessmyfuture.edg.web.rest.dto.PhoneDTO;
import com.guessmyfuture.edg.web.rest.mapper.PhoneMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Phone.
 */
@Service
@Transactional
public class PhoneServiceImpl implements PhoneService{

    private final Logger log = LoggerFactory.getLogger(PhoneServiceImpl.class);

    @Inject
    private PhoneRepository phoneRepository;

    @Inject
    private PhoneMapper phoneMapper;

    @Inject
    private PhoneSearchRepository phoneSearchRepository;

    /**
     * Save a phone.
     *
     * @param phoneDTO the entity to save
     * @return the persisted entity
     */
    @Transactional("secondaryTransactionManager")
    public PhoneDTO save(PhoneDTO phoneDTO) {
        log.debug("Request to save Phone : {}", phoneDTO);
        Phone phone = phoneMapper.phoneDTOToPhone(phoneDTO);
        phone = phoneRepository.save(phone);
        PhoneDTO result = phoneMapper.phoneToPhoneDTO(phone);
        phoneSearchRepository.save(phone);
        return result;
    }

    /**
     *  Get all the phones.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional("secondaryTransactionManager")
    public Page<Phone> findAll(Pageable pageable) {
        log.debug("Request to get all Phones");
        Page<Phone> result = phoneRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one phone by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional("secondaryTransactionManager")
    public PhoneDTO findOne(Long id) {
        log.debug("Request to get Phone : {}", id);
        Phone phone = phoneRepository.findOne(id);
        PhoneDTO phoneDTO = phoneMapper.phoneToPhoneDTO(phone);
        return phoneDTO;
    }

    /**
     *  Delete the  phone by id.
     *
     *  @param id the id of the entity
     */
    @Transactional("secondaryTransactionManager")
    public void delete(Long id) {
        log.debug("Request to delete Phone : {}", id);
        phoneRepository.delete(id);
        phoneSearchRepository.delete(id);
    }

    /**
     * Search for the phone corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional("secondaryTransactionManager")
    public Page<Phone> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Phones for query {}", query);
        return phoneSearchRepository.search(queryStringQuery(query), pageable);
    }
}
