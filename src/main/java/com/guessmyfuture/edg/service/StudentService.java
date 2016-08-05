package com.guessmyfuture.edg.service;

import com.guessmyfuture.edg.domain.Student;
import com.guessmyfuture.edg.web.rest.dto.StudentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.LinkedList;
import java.util.List;

/**
 * Service Interface for managing Student.
 */
public interface StudentService {

    /**
     * Save a student.
     * 
     * @param studentDTO the entity to save
     * @return the persisted entity
     */
    StudentDTO save(StudentDTO studentDTO);

    /**
     *  Get all the students.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<Student> findAll(Pageable pageable);

    /**
     *  Get the "id" student.
     *  
     *  @param id the id of the entity
     *  @return the entity
     */
    StudentDTO findOne(Long id);

    /**
     *  Delete the "id" student.
     *  
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the student corresponding to the query.
     * 
     *  @param query the query of the search
     *  @return the list of entities
     */
    Page<Student> search(String query, Pageable pageable);
}
