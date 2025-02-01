package com.jotech.springmvc;

import com.jotech.springmvc.models.*;
import com.jotech.springmvc.repository.HistoryGradesDao;
import com.jotech.springmvc.repository.MathGradesDao;
import com.jotech.springmvc.repository.ScienceGradesDao;
import com.jotech.springmvc.repository.StudentDao;
import com.jotech.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private MathGradesDao mathGradeDao;

    @Autowired
    private ScienceGradesDao scienceGradeDao;

    @Autowired
    private HistoryGradesDao historyGradeDao;

    @Value("${sql.script.create.student}")
    private String sqlAddStudent;
    @Value("${sql.script.create.math.grade}")
    private String sqlAddMathGrade;
    @Value("${sql.script.create.science.grade}")
    private String sqlAddScienceGrade;
    @Value("${sql.script.create.history.grade}")
    private String sqlAddHistoryGrade;


    @Value("${sql.script.delete.student}")
    private String sqlDeleteStudent;
    @Value("${sql.script.delete.math.grade}")
    private String sqlDeleteMathGrade;
    @Value("${sql.script.delete.science.grade}")
    private String sqlDeleteScienceGrade;
    @Value("${sql.script.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setUpDatabase(){
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @DisplayName("Create Student")
    @Order(1)
    @Test
    public void testCreateStudentService(){

        studentService.createStudent("Aster","Lezeb","aster.lezeb@gmail.com");
        CollegeStudent student = studentDao.findByEmailAddress("aster.lezeb@gmail.com");
        assertEquals("aster.lezeb@gmail.com", student.getEmailAddress(),"find By email");

    }

    @DisplayName("CheckIfNull")
    @Order(2)
    @Test
    public void testIsStudentNullcheck(){

        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @DisplayName("DeleteStudentById")
    @Order(3)
    @Test
    public void testDeleteStudentByService(){

        Optional<CollegeStudent>  deletedStudent =  studentDao.findById(1);
        Optional<MathGrade> deletedMathGrade =  mathGradeDao.findById(1);
        Optional<ScienceGrade> deletedScienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> deletedHistoryGrade = historyGradeDao.findById(1);

        assertTrue(deletedStudent.isPresent(),"must return true");
        assertTrue(deletedMathGrade.isPresent(),"must return true");
        assertTrue(deletedScienceGrade.isPresent(),"must return true");
        assertTrue(deletedHistoryGrade.isPresent(),"must return true");

        studentService.deleteStudentById(1);

        deletedStudent =  studentDao.findById(1);
        deletedMathGrade = mathGradeDao.findById(1);
        deletedScienceGrade = scienceGradeDao.findById(1);
        deletedHistoryGrade = historyGradeDao.findById(1);

        assertFalse(deletedStudent.isPresent(),"Returns false");
       assertFalse(deletedMathGrade.isPresent(),"Returns false");
        assertFalse(deletedScienceGrade.isPresent(),"Returns false");
        assertFalse(deletedHistoryGrade.isPresent(),"Returns false");


    }

    @DisplayName("GetGradeBooksList")
    @Order(4)
    @Sql("/insertData.sql")
    @Test
    public void testGetGradeBookService(){


        Iterable<CollegeStudent> iterableCollegeStudents =  studentService.getGradeBook();
        List<CollegeStudent>  collegeStudents = new ArrayList<>();

        for(CollegeStudent collegeStudent : iterableCollegeStudents){
            collegeStudents.add(collegeStudent);
        }


        assertEquals(5,collegeStudents.size());

    }

    @DisplayName("Create Grade")
    @Order(5)
    @Test
    public void testCreateGradeService(){

        // Create the grade

                          //For Math Grade
        assertTrue(studentService.createGrade(80.50,1,"math"));

                          //For Science  Grade
        assertTrue(studentService.createGrade(80.50,1,"science"));

                          //For History Grade

        assertTrue(studentService.createGrade(80.50,1,"history"));
        // Get all grades with student id
                       //For Math Grade
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
                      //For Science  Grade

        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);

                     //For History Grade
        Iterable<HistoryGrade>  historyGrades = historyGradeDao.findGradeByStudentId(1);

        // Verify there is grade

                           //For Math Grade
        assertTrue(mathGrades.iterator().hasNext(),"Student has math Grade");
        assertTrue( ((Collection<MathGrade>) mathGrades).size()==2,"student has math Grades");

                          //For Science  Grade

        assertTrue(scienceGrades.iterator().hasNext(),"Student has science Grade");
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size()==2,"student has science Grades");

                         //For History Grade

        assertTrue(historyGrades.iterator().hasNext(),"student has history grade");
        assertTrue(((Collection<HistoryGrade>) historyGrades).size()==2,"student has history grades");
    }

    @DisplayName("Invalid inputs Error")
    @Order(6)
    @Test
    public void testCreateGradeServiceReturnFalse(){

        assertFalse(studentService.createGrade(105.0,1,"math"));
        assertFalse(studentService.createGrade(-5,1,"math"));
        assertFalse(studentService.createGrade(80.50,2,"math"));
        assertFalse(studentService.createGrade(80.50,1,"literature"));
    }

    @DisplayName("Delete grade")
    @Order(7)
    @Test
    public void testDeleteGradeService(){

        assertEquals(1,studentService.deleteGrade(1,"math"),
                "returns student id after delete");
        assertEquals(1,studentService.deleteGrade(1,"science"),
                "returns student id after delete");
        assertEquals(1,studentService.deleteGrade(1,"history"),
                "returns student id after delete");
    }

    @DisplayName("Invalid zero id")
    @Order(8)
    @Test
    public void testDeleteGradeServiceReturnStudentIdOfZero(){

        assertEquals(0,studentService.deleteGrade(0,"math"),
                "no student have 0 id");

        assertEquals(0,studentService.deleteGrade(1,"literature"),
                "no student have grade type literature");
    }

    @DisplayName("Student Information")
    @Order(9)
    @Test
    public void testStudentInformation(){

        GradebookCollegeStudent gradebookCollegeStudent = studentService.getStudentInformation(1);

        assertNotNull(gradebookCollegeStudent);
        assertEquals(1,gradebookCollegeStudent.getId());
        assertEquals("Eric",gradebookCollegeStudent.getFirstname());
        assertEquals("Roby",gradebookCollegeStudent.getLastname());
        assertEquals("eric.roby@gmail.com",gradebookCollegeStudent.getEmailAddress());

        assertTrue(gradebookCollegeStudent.getStudentGrades().getMathGradeResults().size()==1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getScienceGradeResults().size()==1);
        assertTrue(gradebookCollegeStudent.getStudentGrades().getHistoryGradeResults().size()==1);
    }
    @DisplayName("Student information Return null")
    @Order(10)
    @Test
    public void testStudentInformationReturnNull(){

        GradebookCollegeStudent gradebookCollegeStudent = studentService.getStudentInformation(0);

        assertNull(gradebookCollegeStudent);
    }


    @AfterEach
    public void setUpAfterTransaction(){

        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);

    }
}
