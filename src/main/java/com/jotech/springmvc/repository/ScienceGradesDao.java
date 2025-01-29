package com.jotech.springmvc.repository;

import com.jotech.springmvc.models.ScienceGrade;
import org.springframework.data.repository.CrudRepository;

public interface ScienceGradesDao extends CrudRepository<ScienceGrade,Integer> {

  public   Iterable<ScienceGrade>  findGradeByStudentId(int id);

  public  void deleteGradeByStudentId(int id);
}
