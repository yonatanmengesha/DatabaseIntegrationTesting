package com.jotech.springmvc.service;

import com.jotech.springmvc.models.CollegeStudent;
import com.jotech.springmvc.models.HistoryGrade;
import com.jotech.springmvc.models.MathGrade;
import com.jotech.springmvc.models.ScienceGrade;
import com.jotech.springmvc.repository.HistoryGradesDao;
import com.jotech.springmvc.repository.MathGradesDao;
import com.jotech.springmvc.repository.ScienceGradesDao;
import com.jotech.springmvc.repository.StudentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class StudentAndGradeService {

    @Autowired
    private  StudentDao studentDao;



    @Autowired
    @Qualifier("mathGrades")
    private MathGrade mathGrade;

    @Autowired
    @Qualifier("scienceGrades")
    private ScienceGrade scienceGrade;

    @Autowired
    @Qualifier("historyGrades")
    private HistoryGrade historyGrade;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    public void createStudent(String firstName,String lastName , String emailAddress){
        CollegeStudent student = new CollegeStudent(firstName,lastName,emailAddress);
        student.setId(0);
        studentDao.save(student);
    }

    public boolean checkIfStudentIsNull(int id) {

        Optional<CollegeStudent> student = studentDao.findById(id);

        if(student.isPresent()){
            return true;
        }

        return false;
    }

    public void deleteStudentById(int id) {

        if(checkIfStudentIsNull(id)){
            studentDao.deleteById(id);
        }

    }

    public Iterable<CollegeStudent> getGradeBook() {

        return studentDao.findAll();
    }

    public CollegeStudent createStudent(CollegeStudent collegeStudent) {

       return  studentDao.save(collegeStudent);
    }

    public boolean createGrade(double grade , int studentId, String gradeType){

        if(!checkIfStudentIsNull(studentId)){
            return false;
        }

        if(grade>0 && grade <=100) {

            if (gradeType.equals("math")) {

                mathGrade.setId(0);
                mathGrade.setGrade(grade);
                mathGrade.setStudentId(studentId);

                mathGradeDao.save(mathGrade);

                return true;
            }

            if(gradeType.equals("science")){

                scienceGrade.setId(0);
                scienceGrade.setGrade(grade);
                scienceGrade.setStudentId(studentId);

                scienceGradeDao.save(scienceGrade);

                return true;
            }

            if(gradeType.equals("history")){

                historyGrade.setId(0);
                historyGrade.setGrade(grade);
                historyGrade.setStudentId(studentId);

                historyGradeDao.save(historyGrade);

                return true;
            }
        }


        return false;

    }

    public int deleteGrade(int id, String gradeType) {

        int studentId =0;

        if(gradeType.equals("math")){

            Optional<MathGrade> grade = mathGradeDao.findById(id);
            if(!grade.isPresent()){
                return studentId;
            }

            studentId = grade.get().getStudentId();
            mathGradeDao.deleteById(id);
        }

        if(gradeType.equals("science")){

            Optional<ScienceGrade> grade = scienceGradeDao.findById(id);
            if(!grade.isPresent()){
                return studentId;
            }

            studentId = grade.get().getStudentId();
            scienceGradeDao.deleteById(id);
        }
        if(gradeType.equals("history")){

            Optional<HistoryGrade> grade = historyGradeDao.findById(id);
            if(!grade.isPresent()){
                return studentId;
            }

            studentId = grade.get().getStudentId();
            historyGradeDao.deleteById(id);
        }

        return studentId;
    }
}
