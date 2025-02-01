package com.jotech.springmvc;

import com.jotech.springmvc.models.CollegeStudent;
import com.jotech.springmvc.models.GradebookCollegeStudent;
import com.jotech.springmvc.models.MathGrade;
import com.jotech.springmvc.repository.MathGradesDao;
import com.jotech.springmvc.repository.StudentDao;
import com.jotech.springmvc.service.StudentAndGradeService;
import io.micrometer.observation.Observation;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource("/application.properties")
//@TestPropertySource(properties = "logging.level.org.springframework.boot.autoconfigure=ERROR")
@TestMethodOrder(MethodOrderer.MethodName.class)
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

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private MathGradesDao mathGradesDao;

    @Mock
    private StudentAndGradeService studentAndGradeServiceMock;

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

    @BeforeAll
    public static void setUp() {
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Chad");
        request.setParameter("lastname", "Derby");
        request.setParameter("emailAddress", "chad.derby@gmail.com");
    }

    @BeforeEach
    public void setUpBeforeEach() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);

    }

    //  @Sql("/insertData.sql")
    @Test
    @Order(1)
    public void testGetStudentsHttpRequest() throws Exception {


        CollegeStudent studentOne = new GradebookCollegeStudent("Eric",
                "Roby", "eric.roby@gmail.com");


        CollegeStudent studentTwo = new GradebookCollegeStudent("Chad",
                "Derby", "chad.derby@gmail.com");


        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne, studentTwo));


        when(studentAndGradeServiceMock.getGradeBook()).thenReturn(collegeStudentList);
        //   System.out.println(studentAndGradeServiceMock.getGradeBook().toString());
        assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradeBook());
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = result.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

    }

    @Test
    @Order(2)
    public void testCreateStudentHttpRequest() throws Exception {

        CollegeStudent studentOne = new GradebookCollegeStudent("Eric",
                "Roby", "eric.roby@gmail.com");

        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne));

        when(studentAndGradeServiceMock.getGradeBook()).thenReturn(collegeStudentList);

        assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradeBook());

        MvcResult mvcResult = mockMvc.perform(post("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("firstname", request.getParameterValues("firstname"))
                        .param("lastname", request.getParameterValues("lastname"))
                        // .param("id",request.getParameterValues("id"))
                        .param("emailAddress", request.getParameterValues("emailAddress")))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");

        CollegeStudent verifyStudent = studentDao.findByEmailAddress("eric.roby@gmail.com");

        assertNotNull(verifyStudent, "student should be found");

    }

    @Test
    @Order(3)
    public void testDeleteStudentHttpRequest() throws Exception {
        assertTrue(studentDao.findById(1).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delete/student/{id}", 1))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "index");

        assertFalse(studentDao.findById(1).isPresent());
    }

    @Test
    @Order(4)
    public void testDeleteStudentHttpRequestErrorPage() throws Exception {
        // assertTrue(studentDao.findById(0).isPresent());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/delete/student/{id}", 0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");

        //  assertFalse(studentDao.findById(0).isPresent());
    }


    @DisplayName("Test Valid Student ID")
    @Order(5)
    @Test
    public void testStudentInformationHttpRequest() throws Exception {

        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/studentInformation/{id}", 1))
                .andExpect(status().isOk()).andReturn();
        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");
    }

    @Test
    @Order(6)
    public void testStudentInformationHttpStudentDoesNotExistRequest() throws Exception {
        assertFalse(studentDao.findById(0).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                        .get("/studentInformation/{id}", 0))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @Order(7)
    public void testCreateValidGradeHttpRequest() throws Exception {

        assertTrue(studentDao.findById(1).isPresent());

        GradebookCollegeStudent student = studentService.getStudentInformation(1);

        assertEquals(1, student.getStudentGrades().getMathGradeResults().size());

        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                .contentType(MediaType.APPLICATION_JSON)
                .param("grade", "85.00")
                .param("gradeType", "math")
                .param("studentId", "1")).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "studentInformation");

        student = studentService.getStudentInformation(1);

        assertEquals(2, student.getStudentGrades().getMathGradeResults().size());

    }

    @Order(8)
    @Test
    public void testCreateValidGradeHttpRequestStudentDoesNotExistEmptyResponse() throws Exception {

        MvcResult mvcResult = this.mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "math")
                        .param("studentId", "0"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @Order(9)
    public void testCreateANonValidGradeHttpRequestStudentDoesNotExistEmptyResponse() throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/grades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("grade", "85.00")
                        .param("gradeType", "literature")
                        .param("studentId", "1"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @Test
    @Order(10)
    public void testDeleteAValidGradeHttpRequest() throws Exception{

       Optional<MathGrade> mathGrade = mathGradesDao.findById(1);

       assertTrue( mathGrade.isPresent());

       MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
               .get("/grades/{id}/{gradeType}",1,"math"))
                       .andExpect(status().isOk()).andReturn();

       ModelAndView mav = mvcResult.getModelAndView();
       ModelAndViewAssert.assertViewName(mav,"studentInformation");

       mathGrade =  mathGradesDao.findById(1);
       assertFalse(mathGrade.isPresent());



    }


    @Test
    @Order(11)
    public void testDeleteAValidGradeHttpRequestStudentIdDoesNotExistEmptyResponse() throws Exception{

        Optional<MathGrade> mathGrade = mathGradesDao.findById(2);
        assertFalse(mathGrade.isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/grades/{id}/{gradeType}",2,"math"))
                .andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");

        mathGrade = mathGradesDao.findById(0);
        assertFalse(mathGrade.isPresent());

    }

    @Test
    @Order(12)
    public void testDeleteANonValidGradeHttpRequestStudentIdDoesNotExistEmptyResponse() throws Exception{

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
                .get("/grades/{id}/{gradeType}",1,"literature"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView mav = mvcResult.getModelAndView();
        ModelAndViewAssert.assertViewName(mav,"error");
    }

    @AfterEach
    public void setUpAfterTransaction() {

        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }
}
