package com.lambdaschool.school.controller;

import com.lambdaschool.school.model.ErrorDetail;
import com.lambdaschool.school.model.Student;
import com.lambdaschool.school.service.StudentService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/students")
public class StudentController
{

    @Autowired
    private StudentService studentService;
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    private void Log(HttpServletRequest request)
    {
        logger.info(request.getMethod() + " " + request.getRequestURI() + " Accessed");
    }


    // Please note there is no way to add students to course yet!
    @ApiOperation(value = "Lists all Students",responseContainer = "List")
    @ApiImplicitParams({
            @ApiImplicitParam(
                    name = "page",
                    dataType = "integer",
                    paramType = "query",
                    value = "Results page you want to retrieve (0..N)"),
            @ApiImplicitParam(
                    name = "size",
                    dataType = "integer",
                    paramType = "query",
                    value = "Number of records per page."),
            @ApiImplicitParam(
                    name = "sort",
                    allowMultiple = true,
                    dataType = "string",
                    paramType = "query",
                    value = "Sorting criteria in the format: property(,asc|desc). " +
                            "Default sort order is ascending. " +
                            "Multiple sort criteria are supported.")})
    @GetMapping(value = "/students", produces = {"application/json"})
    public ResponseEntity<?> listAllStudents(
            HttpServletRequest request,
        @PathVariable
        @PageableDefault(page = 0, size = 3) Pageable pageable)
    {
        Log(request);
        List<Student> myStudents = studentService.findAll();
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }
    @ApiOperation(value = "Get a Student given it's id", response = Student.class)
    @ApiResponses(value = {
            @ApiResponse(code=404 ,message = "Student Not Found", response = ErrorDetail.class),
            @ApiResponse(code=401 ,message = "Not Authorized", response = ErrorDetail.class)
    })
    @GetMapping(value = "/Student/{StudentId}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentById(
            HttpServletRequest request,
            @ApiParam(value = "Student id", required = true, example = "1")
            @PathVariable
                    Long StudentId)
    {
        Log(request);
        Student r = studentService.findStudentById(StudentId);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }

    @ApiOperation(value = "Search for a student by the name container", responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code=404,message = "Student Not Found", response = ErrorDetail.class),
            @ApiResponse(code=401,message = "Not Authorized", response = ErrorDetail.class),

    })
    @GetMapping(value = "/student/namelike/{name}",
                produces = {"application/json"})
    public ResponseEntity<?> getStudentByNameContaining(
            HttpServletRequest request,
            @PathVariable String name)
    {
        Log(request);
        List<Student> myStudents = studentService.findStudentByNameLike(name);
        return new ResponseEntity<>(myStudents, HttpStatus.OK);
    }


    ///////
    @ApiOperation(value="Add Student", response = void.class, notes = "Location of new Student in Location header")
    @ApiResponses(value = {
            @ApiResponse(code=401,message = "Not allowed", response = ErrorDetail.class),
            @ApiResponse(code=500,message = "Error in adding student", response = ErrorDetail.class),
            @ApiResponse(code=400,message = "Incorrect fields", response = ErrorDetail.class),
            @ApiResponse(code = 404, message = "Not Found", response = ErrorDetail.class)
    })
    @PostMapping(value = "/Student",

                 consumes = {"application/json"},
                 produces = {"application/json"})
    public ResponseEntity<?> addNewStudent( HttpServletRequest request,
                                            @Valid
                                           @RequestBody
                                                   Student newStudent) throws URISyntaxException
    {
        Log(request);

        newStudent = studentService.save(newStudent);

        // set the location header for the newly created resource
        HttpHeaders responseHeaders = new HttpHeaders();
        URI newStudentURI = ServletUriComponentsBuilder.fromCurrentRequest().path("/{Studentid}").buildAndExpand(newStudent.getStudid()).toUri();
        responseHeaders.setLocation(newStudentURI);

        return new ResponseEntity<>(null, responseHeaders, HttpStatus.CREATED);
    }
//////
    @ApiOperation(value = "Edit a Student using their Id", response = void.class)
    @PutMapping(value = "/Student/{Studentid}")
    public ResponseEntity<?> updateStudent(
            @RequestBody
                    Student updateStudent,
            @PathVariable
                    long Studentid)
    {
        studentService.update(updateStudent, Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiOperation(value = "Delete Student going by their Id", response = void.class)
    @ApiResponses(value = {
            @ApiResponse(code=401, message = "Not Authorized", response = ErrorDetail.class),
            @ApiResponse(code = 404, message = "Not Found", response = ErrorDetail.class),
            @ApiResponse(code=500,message = "Error Deleting", response = ErrorDetail.class)
    })
    @DeleteMapping("/Student/{Studentid}")
    public ResponseEntity<?> deleteStudentById(
            @PathVariable
                    long Studentid)
    {
        studentService.delete(Studentid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
