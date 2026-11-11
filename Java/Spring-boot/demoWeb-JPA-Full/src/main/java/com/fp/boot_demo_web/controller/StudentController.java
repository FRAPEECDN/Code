package com.fp.boot_demo_web.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fp.boot_demo_web.model.dto.StudentDto;
import com.fp.boot_demo_web.model.dto.StudentProfileDto;
import com.fp.boot_demo_web.service.StudentService;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** HTTP API for student CRUD, current course registrations, and profiles. */
@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student, course-registration, and profile operations")
public class StudentController {

    private final StudentService studentService;

    @GetMapping
    @Operation(summary = "List students", description = "Returns all students, including current course IDs and the optional profile ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Students returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = StudentDto.class)))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public List<StudentDto> getAll() {
        return studentService.getAll();
    }

    @GetMapping("/{studentId}")
    @Operation(summary = "Get a student")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
            @ApiResponse(responseCode = "400", description = "Student ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Student ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentDto getOne(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId) {
        return studentService.getOne(studentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a student", description = "Creates a student and optionally registers existing courses. Create a profile separately.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Student created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
            @ApiResponse(responseCode = "400", description = "Request body is malformed or fails DTO validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "A requested course does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Body includes a generated ID or profile ID",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentDto createOne(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Student fields and optional current course IDs; omit the generated ID and manage the profile separately",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StudentDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":null,\"name\":\"Alex Morgan\",\"academicYear\":2,\"active\":true,\"profileId\":null,\"courseIds\":[]}")))
            @Valid @RequestBody StudentDto input) {
        return studentService.createOne(input);
    }

    @PutMapping("/{studentId}")
    @Operation(summary = "Update a student's details and current course registrations",
            description = "Replaces scalar fields and the current course ID set. Profile changes use the profile routes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
            @ApiResponse(responseCode = "400", description = "Path ID is not an integer or the request body fails validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student or a requested course does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Path/body ID conflict or profile changes attempted here",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentDto updateOne(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Replacement student fields and complete current course ID set; profile changes use the profile routes",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StudentDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"name\":\"Alex Morgan\",\"academicYear\":2,\"active\":true,\"profileId\":null,\"courseIds\":[]}")))
            @Valid @RequestBody StudentDto input) {
        return studentService.updateOne(studentId, input);
    }

    @DeleteMapping("/{studentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a student")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Student deleted; response has no body"),
            @ApiResponse(responseCode = "400", description = "Student ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Student ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public void deleteOne(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId) {
        studentService.deleteOne(studentId);
    }

    @PostMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "Register a student for a course",
            description = "Adds a current course registration and returns the updated student. Repeating an existing registration is harmless.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration completed; updated student returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
            @ApiResponse(responseCode = "400", description = "A path ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student or course does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "A path ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentDto registerForCourse(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId,
            @Parameter(name = "courseId", description = "Positive course identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("courseId") int courseId) {
        return studentService.registerForCourse(studentId, courseId);
    }

    @DeleteMapping("/{studentId}/courses/{courseId}")
    @Operation(summary = "Deregister a student from a course",
            description = "Removes a current course registration and returns the updated student. Repeating the operation is harmless.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Deregistration completed; updated student returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentDto.class))),
            @ApiResponse(responseCode = "400", description = "A path ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student or course does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "A path ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentDto deregisterFromCourse(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId,
            @Parameter(name = "courseId", description = "Positive course identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("courseId") int courseId) {
        return studentService.deregisterFromCourse(studentId, courseId);
    }

    @GetMapping("/{studentId}/profile")
    @Operation(summary = "Get a student's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Student profile returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Student ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student or profile does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Student ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentProfileDto getProfile(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId) {
        return studentService.getProfileForStudent(studentId);
    }

    @PostMapping("/{studentId}/profile")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a student's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Profile created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Path ID is not an integer or request body fails validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Generated profile ID supplied, profile studentId differs from the path, or the student already has a profile",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentProfileDto createProfile(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Profile data whose studentId must match the path; omit the generated profile ID",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StudentProfileDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":null,\"email\":\"alex@example.com\",\"phone\":\"+1-555-0100\",\"studentId\":1}")))
            @Valid @RequestBody StudentProfileDto input) {
        requireProfileStudentId(studentId, input);
        return studentService.createProfile(input);
    }

    @PutMapping("/{studentId}/profile")
    @Operation(summary = "Update a student's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profile updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = StudentProfileDto.class))),
            @ApiResponse(responseCode = "400", description = "Path ID is not an integer or request body fails validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student or profile does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Profile or student ID differs from the requested resource",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public StudentProfileDto updateProfile(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Replacement profile data; studentId must match the path and profile ID must match the current profile",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StudentProfileDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"email\":\"alex.updated@example.com\",\"phone\":null,\"studentId\":1}")))
            @Valid @RequestBody StudentProfileDto input) {
        requireProfileStudentId(studentId, input);
        return studentService.updateProfileForStudent(studentId, input);
    }

    @DeleteMapping("/{studentId}/profile")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a student's profile")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Profile deleted; response has no body"),
            @ApiResponse(responseCode = "400", description = "Student ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Student or profile does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Student ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error")
    })
    public void deleteProfile(
            @Parameter(name = "studentId", description = "Positive student identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("studentId") int studentId) {
        studentService.deleteProfileForStudent(studentId);
    }

    private static void requireProfileStudentId(int studentId, StudentProfileDto input) {
        if (input.studentId() != studentId) {
            throw new IllegalArgumentException("Profile studentId must match the requested student");
        }
    }
}
