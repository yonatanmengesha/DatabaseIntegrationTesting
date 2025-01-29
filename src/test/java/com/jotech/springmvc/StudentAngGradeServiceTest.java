package com.jotech.springmvc;

import com.jotech.springmvc.models.CollegeStudent;
import com.jotech.springmvc.models.HistoryGrade;
import com.jotech.springmvc.models.MathGrade;
import com.jotech.springmvc.models.ScienceGrade;
import com.jotech.springmvc.repository.HistoryGradesDao;
import com.jotech.springmvc.repository.MathGradesDao;
import com.jotech.springmvc.repository.ScienceGradesDao;
import com.jotech.springmvc.repository.StudentDao;
import com.jotech.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestPropertySource("/application.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class StudentAngGradeServiceTest {

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

    @BeforeEach
    public void setUpDatabase(){
        jdbc.execute("insert into student(firstname,lastname,email_address) " +
                "values('Eric','Roby','eric.roby@gmail.com')");

        jdbc.execute("insert into math_grade(student_id,grade) values(1,100.00)");
        jdbc.execute("insert into science_grade(student_id,grade) values(1,100.00)");
        jdbc.execute("insert into history_grade(student_id,grade) values(1,100.00)");
    }

    @DisplayName("Create Student")
    @Order(1)
    @Test
    public void testCreateStudentService(){

        studentService.createStudent("Aster","Lezeb","aster.lezeb@gmail.com");
        CollegeStudent student = studentDao.findByEmailAddress("aster.lezeb@gmail.com");
        assertEquals("aster.lezeb@gmail.com", student.getEmailAddress(),"find By eamil");

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

        assertTrue(deletedStudent.isPresent(),"must return true");

        studentService.deleteStudentById(1);

        deletedStudent =  studentDao.findById(1);

        assertFalse(deletedStudent.isPresent(),"Returns false");

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


    @AfterEach
    public void setUpAfterTransaction(){

        jdbc.execute("DELETE FROM student");
        jdbc.execute("DELETE FROM math_grade");
        jdbc.execute("DELETE FROM science_grade");
        jdbc.execute("DELETE FROM history_grade");

        jdbc.execute("ALTER TABLE student ALTER COLUMN ID RESTART WITH 1");
        jdbc.execute("ALTER TABLE math_grade ALTER COLUMN ID RESTART WITH 1");
        jdbc.execute("ALTER TABLE science_grade ALTER COLUMN ID RESTART WITH 1");
        jdbc.execute("ALTER TABLE history_grade ALTER COLUMN ID RESTART WITH 1");
    }
}
