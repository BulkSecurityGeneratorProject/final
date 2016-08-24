package com.guessmyfuture.edg.repository;

import com.guessmyfuture.edg.domain.Blogger;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Blogger entity.
 */
@SuppressWarnings("unused")
public interface BloggerRepository extends JpaRepository<Blogger,Long> {

}
