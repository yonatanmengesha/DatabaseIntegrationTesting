package com.jotech.springmvc;

import com.jotech.springmvc.models.CollegeStudent;
import com.jotech.springmvc.models.GradebookCollegeStudent;
import com.jotech.springmvc.repository.StudentDao;
import com.jotech.springmvc.service.StudentAndGradeService;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest()//classes = MvcTestingExampleApplication.class)
public class GradeBookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentDao studentDao;

     @Mock
     private StudentAndGradeService studentAndGradeServiceMock;

    @BeforeAll
    public static void setUp(){
        request = new MockHttpServletRequest();
        request.setParameter("firstname","Chad");
        request.setParameter("lastname","Derby");
        request.setParameter("emailAddress","chad.derby@gmail.com");
    }
    @BeforeEach
    public void setUpBeforeEach(){



      jdbc.execute("insert into student(id,firstname,lastname,email_address) " +
                "values(2,'Eric','Roby','eric.roby@gmail.com')");

//        studentAndGradeServiceMock.createStudent("Eric",
//                "Roby","eric.roby@gmail.com");
//        studentAndGradeServiceMock.createStudent("Chad",
//                        "Derby","chad.derby@gmail.com");

    }

  //  @Sql("/insertData.sql")
    @Test
    public void testGetStudentsHttpRequest() throws Exception{

//     for(CollegeStudent s : studentAndGradeServiceMock.getGradeBook()){
//         System.out.println(s);
//     }

//        System.out.println(studentAndGradeServiceMock.getGradeBook().toString());


        CollegeStudent studentOne = new GradebookCollegeStudent("Eric",
                "Roby","eric.roby@gmail.com");


       CollegeStudent studentTwo = new GradebookCollegeStudent("Chad",
               "Derby","chad.derby@gmail.com");



        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne,studentTwo));


       when(studentAndGradeServiceMock.getGradeBook()).thenReturn(collegeStudentList);
     //   System.out.println(studentAndGradeServiceMock.getGradeBook().toString());
       assertIterableEquals(collegeStudentList,studentAndGradeServiceMock.getGradeBook());
       MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav ,"index");

    }

    @Test
    public void testCreateStudentHttpRequest() throws  Exception{

        CollegeStudent studentOne = new GradebookCollegeStudent("Eric",
                "Roby","eric.roby@gmail.com");

        List<CollegeStudent> collegeStudentList =  new ArrayList<>(Arrays.asList(studentOne));

        when(studentAndGradeServiceMock.getGradeBook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList,studentAndGradeServiceMock.getGradeBook());

        MvcResult mvcResult = mockMvc.perform(post("/")
                .contentType(MediaType.APPLICATION_JSON)
                .param("firstname" , request.getParameterValues("firstname"))
                .param("lastname" , request.getParameterValues("lastname"))
                       // .param("id",request.getParameterValues("id"))
                .param("emailAddress" , request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav =  mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"index");

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("eric.roby@gmail.com");

        assertNotNull(verifyStudent,"student should be found");

    }

    @Test
    public void testDeleteStudentHttpRequest() throws Exception{
        assertTrue(studentDao.findById(2).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/delete/student/{id}",2))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"index");

        assertFalse(studentDao.findById(2).isPresent());
    }

    @Test
    public void testDeleteStudentHttpRequestErrorPage() throws Exception{
       // assertTrue(studentDao.findById(0).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delete/student/{id}",0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");

      //  assertFalse(studentDao.findById(0).isPresent());
    }

    @AfterEach
    public void setUpAfterTransaction(){

        jdbc.execute("DELETE FROM student");
   //     jdbc.execute("ALTER TABLE student ALTER COLUMN ID RESTART WITH 1");
    }
}
