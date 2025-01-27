package com.jotech.springmvc.repository;

import com.jotech.springmvc.models.CollegeStudent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StudentDao extends CrudRepository<CollegeStudent,Integer> {

    public CollegeStudent findByEmailAddress(String emailAddress);
}
