package com.jotech.springmvc.repository;

import com.jotech.springmvc.models.MathGrade;
import org.springframework.data.repository.CrudRepository;

public interface MathGradesDao extends CrudRepository<MathGrade,Integer> {

    public Iterable<MathGrade> findGradeByStudentId(int id);
}
