package com.jotech.springmvc.controller;

import com.jotech.springmvc.models.CollegeStudent;
import com.jotech.springmvc.models.Gradebook;
import com.jotech.springmvc.service.StudentAndGradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService studentService;


	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {

		Iterable<CollegeStudent> collegeStudents = studentService.getGradeBook();
		m.addAttribute("students",collegeStudents);
		return "index";
	}

    @PostMapping("/")
	public String createStudent(@ModelAttribute("collegeStudent") CollegeStudent collegeStudent,Model model){

	 studentService.createStudent(collegeStudent.getFirstname(),collegeStudent.getLastname()
			 , collegeStudent.getEmailAddress());

	        Iterable<CollegeStudent>   collegeStudents =     studentService.getGradeBook();
        model.addAttribute("students",collegeStudents);

		return "index";
	}

	@GetMapping("/delete/student/{id}")
	public String deleteStudentById(@PathVariable int id ,Model m){

		if(!studentService.checkIfStudentIsNull(id)){

			return "error";
		}

		studentService.deleteStudentById(id);

		Iterable<CollegeStudent> collegeStudents = studentService.getGradeBook();

		m.addAttribute("students",collegeStudents);

		return "index";

	}

	@GetMapping("/studentInformation/{id}")
		public String studentInformation(@PathVariable int id, Model m) {
		return "studentInformation";
		}

}
