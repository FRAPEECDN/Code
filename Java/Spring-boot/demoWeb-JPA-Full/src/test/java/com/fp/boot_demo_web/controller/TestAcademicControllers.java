package com.fp.boot_demo_web.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fp.boot_demo_web.model.dto.CourseDto;
import com.fp.boot_demo_web.model.dto.DepartmentDto;
import com.fp.boot_demo_web.model.dto.StudentDto;
import com.fp.boot_demo_web.model.dto.StudentProfileDto;
import com.fp.boot_demo_web.model.enums.CourseLevel;
import com.fp.boot_demo_web.model.enums.DepartmentType;
import com.google.gson.Gson;

/** Full-context HTTP tests for student, department, course, and profile routes. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestAcademicControllers {

    private static final String STUDENTS = "/api/students";
    private static final String DEPARTMENTS = "/api/departments";

    private final Gson gson = new Gson();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void clearAcademicTables() {
        jdbcTemplate.update("DELETE FROM student_course");
        jdbcTemplate.update("DELETE FROM student_profile");
        jdbcTemplate.update("DELETE FROM course");
        jdbcTemplate.update("DELETE FROM student");
        jdbcTemplate.update("DELETE FROM department");
    }

    @Test
    void completesStudentRegistrationAndProfileLifecycleOverHttp() throws Exception {
        DepartmentDto department = createDepartment("Student API", DepartmentType.SCIENCE);
        CourseDto course = createCourse(department.id(), "STU-101");
        StudentDto student = createStudent("Alex Student");

        mockMvc.perform(get(STUDENTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)));
        mockMvc.perform(get(STUDENTS + "/{studentId}", student.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.academicYear", equalTo(1)));
        mockMvc.perform(put(STUDENTS + "/{studentId}", student.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new StudentDto(student.id(), "Alex Morgan", 2, false, null, Set.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Alex Morgan")))
                .andExpect(jsonPath("$.academicYear", equalTo(2)));

        mockMvc.perform(post(STUDENTS + "/{studentId}/courses/{courseId}", student.id(), course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseIds[0]", equalTo(course.id())));
        mockMvc.perform(get(DEPARTMENTS + "/{departmentId}/courses/{courseId}", department.id(), course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentIds[0]", equalTo(student.id())));

        StudentProfileDto profile = new StudentProfileDto(null, "alex@example.test", "555-0100", student.id());
        String createdProfileJson = mockMvc.perform(post(STUDENTS + "/{studentId}/profile", student.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(profile)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", equalTo("alex@example.test")))
                .andReturn()
                .getResponse()
                .getContentAsString();
        StudentProfileDto createdProfile = gson.fromJson(createdProfileJson, StudentProfileDto.class);

        mockMvc.perform(get(STUDENTS + "/{studentId}/profile", student.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(createdProfile.id())));
        mockMvc.perform(put(STUDENTS + "/{studentId}/profile", student.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new StudentProfileDto(createdProfile.id(),
                        "alex.updated@example.test", null, student.id()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", equalTo("alex.updated@example.test")));
        mockMvc.perform(delete(STUDENTS + "/{studentId}/profile", student.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(STUDENTS + "/{studentId}/courses/{courseId}", student.id(), course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseIds").isEmpty());
        mockMvc.perform(delete(DEPARTMENTS + "/{departmentId}/courses/{courseId}", department.id(), course.id()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete(DEPARTMENTS + "/{departmentId}", department.id()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete(STUDENTS + "/{studentId}", student.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get(STUDENTS + "/{studentId}", student.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void managesDepartmentsAndMovesCoursesWhileProtectingRegistrations() throws Exception {
        DepartmentDto source = createDepartment("Source Department", DepartmentType.SCIENCE);
        DepartmentDto destination = createDepartment("Destination Department", DepartmentType.ARTS);

        mockMvc.perform(get(DEPARTMENTS))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(2)));
        mockMvc.perform(get(DEPARTMENTS + "/{departmentId}", source.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Source Department")));
        mockMvc.perform(put(DEPARTMENTS + "/{departmentId}", source.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new DepartmentDto(source.id(), "Updated Source",
                        DepartmentType.BUSINESS, Set.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Updated Source")));

        CourseDto course = createCourse(source.id(), "MOVE-101");
        mockMvc.perform(get(DEPARTMENTS + "/{departmentId}/courses", source.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(1)));
        mockMvc.perform(get(DEPARTMENTS + "/{departmentId}/courses/{courseId}", source.id(), course.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", equalTo("MOVE-101")));

        mockMvc.perform(put(DEPARTMENTS + "/{departmentId}/courses/{courseId}", source.id(), course.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new CourseDto(course.id(), "MOVE-201", "Moved Course",
                        200.0, true, CourseLevel.INTERMEDIATE, destination.id(), Set.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentId", equalTo(destination.id())));
        mockMvc.perform(put(DEPARTMENTS + "/{departmentId}", destination.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new DepartmentDto(destination.id(), "Updated Destination",
                        DepartmentType.ARTS, Set.of(course.id())))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseIds[0]", equalTo(course.id())));

        StudentDto student = createStudent("Registered Student");
        mockMvc.perform(post(STUDENTS + "/{studentId}/courses/{courseId}", student.id(), course.id()))
                .andExpect(status().isOk());
        mockMvc.perform(delete(DEPARTMENTS + "/{departmentId}/courses/{courseId}", destination.id(), course.id()))
                .andExpect(status().isConflict());
        mockMvc.perform(delete(DEPARTMENTS + "/{departmentId}", destination.id()))
                .andExpect(status().isConflict());

        mockMvc.perform(delete(STUDENTS + "/{studentId}/courses/{courseId}", student.id(), course.id()))
                .andExpect(status().isOk());
        mockMvc.perform(delete(DEPARTMENTS + "/{departmentId}/courses/{courseId}", destination.id(), course.id()))
                .andExpect(status().isNoContent());
        mockMvc.perform(get(DEPARTMENTS + "/{departmentId}/courses", destination.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", equalTo(0)));
        mockMvc.perform(delete(DEPARTMENTS + "/{departmentId}", destination.id()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete(DEPARTMENTS + "/{departmentId}", source.id()))
                .andExpect(status().isNoContent());
        mockMvc.perform(delete(STUDENTS + "/{studentId}", student.id()))
                .andExpect(status().isNoContent());
    }

    @Test
    void validatesRequestDtosAndRelationshipPathIds() throws Exception {
        mockMvc.perform(post(STUDENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new StudentDto(null, " ", 1, true, null, Set.of()))))
                .andExpect(status().isBadRequest());

        DepartmentDto department = createDepartment("Path Validation", DepartmentType.SCIENCE);
        mockMvc.perform(post(DEPARTMENTS + "/{departmentId}/courses", department.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new CourseDto(null, "PATH-101", "Path Course", 100.0,
                        true, CourseLevel.INTRODUCTORY, department.id() + 1, Set.of()))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$", equalTo("Internal conflict happened.")));

        StudentDto student = createStudent("Path Student");
        mockMvc.perform(post(STUDENTS + "/{studentId}/profile", student.id())
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new StudentProfileDto(null, "path@example.test", null, student.id() + 1))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$", equalTo("Internal conflict happened.")));
    }

    @Test
    void publishesExplicitAcademicOpenApiParametersAndResponses() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/students/{studentId}'].get.parameters[0].name",
                        equalTo("studentId")))
                .andExpect(jsonPath("$.paths['/api/students/{studentId}'].get.parameters[0].schema.minimum",
                        equalTo(1)))
                .andExpect(jsonPath("$.paths['/api/students/{studentId}'].get.responses['200'].content['application/json'].schema.$ref")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/students/{studentId}'].get.responses['400'].content['application/problem+json'].schema.$ref")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/students/{studentId}'].get.responses['404'].content['application/json'].schema.type",
                        equalTo("string")))
                .andExpect(jsonPath("$.paths['/api/students/{studentId}'].get.responses['500'].content['application/json']")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/departments/{departmentId}/courses/{courseId}'].put.responses['200'].content['application/json'].schema.$ref")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/departments/{departmentId}/courses/{courseId}'].delete.responses['204'].content")
                        .doesNotExist())
                .andExpect(jsonPath("$.paths['/api/simple'].post.responses['201'].content['application/json'].schema.$ref")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/simple/{id}'].delete.responses['204'].content")
                        .doesNotExist())
                .andExpect(jsonPath("$.paths['/api/simple/{id}'].patch.requestBody.content['application/json'].schema.$ref")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/simple/search'].get.parameters[0].schema.minimum")
                        .exists())
                .andExpect(jsonPath("$.paths['/api/simple/search'].get.responses['200'].content['application/json'].schema.$ref",
                        equalTo("#/components/schemas/SimplePageResponse")))
                .andExpect(jsonPath("$.components.schemas.SimplePageResponse.properties.content.items.$ref",
                        equalTo("#/components/schemas/SimpleDto")))
                .andExpect(jsonPath("$.components.schemas.SimplePageResponse.properties.totalElements.type",
                        equalTo("integer")));
    }

    private DepartmentDto createDepartment(String name, DepartmentType type) throws Exception {
        String response = mockMvc.perform(post(DEPARTMENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new DepartmentDto(null, name, type, Set.of()))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return gson.fromJson(response, DepartmentDto.class);
    }

    private CourseDto createCourse(int departmentId, String code) throws Exception {
        String response = mockMvc.perform(post(DEPARTMENTS + "/{departmentId}/courses", departmentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new CourseDto(null, code, code + " Foundations",
                        100.0, true, CourseLevel.INTRODUCTORY, departmentId, Set.of()))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return gson.fromJson(response, CourseDto.class);
    }

    private StudentDto createStudent(String name) throws Exception {
        MvcResult result = mockMvc.perform(post(STUDENTS)
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new StudentDto(null, name, 1, true, null, Set.of()))))
                .andExpect(status().isCreated())
                .andReturn();
        return gson.fromJson(result.getResponse().getContentAsString(), StudentDto.class);
    }
}
