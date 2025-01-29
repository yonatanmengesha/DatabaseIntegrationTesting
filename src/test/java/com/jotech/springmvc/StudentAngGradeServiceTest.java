package com.jotech.springmvc;

import com.jotech.springmvc.models.CollegeStudent;
import com.jotech.springmvc.repository.StudentDao;
import com.jotech.springmvc.service.StudentAndGradeService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
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

    @BeforeEach
    public void setUpDatabase(){
        jdbc.execute("insert into student(firstname,lastname,email_address) " +
                "values('Eric','Roby','eric.roby@gmail.com')");
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
    @Order(3)
    @Test
    public void testIsStudentNullcheck(){

        assertTrue(studentService.checkIfStudentIsNull(1));
        assertFalse(studentService.checkIfStudentIsNull(0));
    }

    @DisplayName("DeleteStudentById")
    @Order(4)
    @Test
    public void testDeleteStudentByService(){

        Optional<CollegeStudent>  deletedStudent =  studentDao.findById(1);

        assertTrue(deletedStudent.isPresent(),"must return true");

        studentService.deleteStudentById(1);

        deletedStudent =  studentDao.findById(1);

        assertFalse(deletedStudent.isPresent(),"Returns false");

    }

    @DisplayName("GetGradeBooksList")
    @Order(2)
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


    @AfterEach
    public void setUpAfterTransaction(){

        jdbc.execute("DELETE FROM student");
        jdbc.execute("ALTER TABLE student ALTER COLUMN ID RESTART WITH 1");
    }
}
