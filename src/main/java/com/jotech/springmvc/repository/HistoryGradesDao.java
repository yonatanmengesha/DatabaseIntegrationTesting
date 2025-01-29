package com.jotech.springmvc.repository;

import com.jotech.springmvc.models.HistoryGrade;
import org.springframework.data.repository.CrudRepository;

public interface HistoryGradesDao  extends CrudRepository<HistoryGrade,Integer> {


    public Iterable<HistoryGrade>   findGradeByStudentId(int id);
}
