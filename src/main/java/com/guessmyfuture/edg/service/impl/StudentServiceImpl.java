package com.guessmyfuture.edg.service.impl;

import com.guessmyfuture.edg.service.StudentService;
import com.guessmyfuture.edg.domain.Student;
import com.guessmyfuture.edg.repository.StudentRepository;
import com.guessmyfuture.edg.repository.search.StudentSearchRepository;
import com.guessmyfuture.edg.web.rest.dto.StudentDTO;
import com.guessmyfuture.edg.web.rest.mapper.StudentMapper;
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
 * Service Implementation for managing Student.
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService{

    private final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);
    
    @Inject
    private StudentRepository studentRepository;
    
    @Inject
    private StudentMapper studentMapper;
    
    @Inject
    private StudentSearchRepository studentSearchRepository;
    
    /**
     * Save a student.
     * 
     * @param studentDTO the entity to save
     * @return the persisted entity
     */
    public StudentDTO save(StudentDTO studentDTO) {
        log.debug("Request to save Student : {}", studentDTO);
        Student student = studentMapper.studentDTOToStudent(studentDTO);
        student = studentRepository.save(student);
        StudentDTO result = studentMapper.studentToStudentDTO(student);
        studentSearchRepository.save(student);
        return result;
    }

    /**
     *  Get all the students.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Student> findAll(Pageable pageable) {
        log.debug("Request to get all Students");
        Page<Student> result = studentRepository.findAll(pageable); 
        return result;
    }

    /**
     *  Get one student by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public StudentDTO findOne(Long id) {
        log.debug("Request to get Student : {}", id);
        Student student = studentRepository.findOne(id);
        StudentDTO studentDTO = studentMapper.studentToStudentDTO(student);
        return studentDTO;
    }

    /**
     *  Delete the  student by id.
     *  
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Student : {}", id);
        studentRepository.delete(id);
        studentSearchRepository.delete(id);
    }

    /**
     * Search for the student corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<Student> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Students for query {}", query);
        return studentSearchRepository.search(queryStringQuery(query), pageable);
    }
}
