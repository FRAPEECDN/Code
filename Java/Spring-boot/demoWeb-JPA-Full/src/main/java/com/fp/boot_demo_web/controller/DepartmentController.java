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

import com.fp.boot_demo_web.model.dto.CourseDto;
import com.fp.boot_demo_web.model.dto.DepartmentDto;
import com.fp.boot_demo_web.service.DepartmentService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/** HTTP API for department CRUD and its course lifecycle. */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Department and course operations")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    @Operation(summary = "List departments", description = "Returns all departments and their course IDs.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departments returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DepartmentDto.class)))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public List<DepartmentDto> getAll() {
        return departmentService.getAll();
    }

    @GetMapping("/{departmentId}")
    @Operation(summary = "Get a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepartmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Department ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Department does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Department ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public DepartmentDto getOne(
            @Parameter(name = "departmentId", description = "Positive department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId) {
        return departmentService.getOne(departmentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a department", description = "Creates a department without courses. Create courses through the nested course route.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Department created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepartmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Request body is malformed or fails DTO validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Generated ID supplied or courseIds is not empty",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public DepartmentDto createOne(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Department fields; omit the generated ID and use an empty courseIds array",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepartmentDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":null,\"name\":\"Computer Science\",\"type\":\"SCIENCE\",\"courseIds\":[]}")))
            @Valid @RequestBody DepartmentDto input) {
        return departmentService.createOne(input);
    }

    @PutMapping("/{departmentId}")
    @Operation(summary = "Update department details",
            description = "Updates scalar fields only. courseIds must remain unchanged; manage courses through the nested course routes.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Department updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = DepartmentDto.class))),
            @ApiResponse(responseCode = "400", description = "Path ID is not an integer or request body fails validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Department does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Path/body ID conflict or courseIds changed in this operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public DepartmentDto updateOne(
            @Parameter(name = "departmentId", description = "Positive department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Replacement department fields; courseIds must equal the department's current course IDs",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepartmentDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"name\":\"Computer Science\",\"type\":\"SCIENCE\",\"courseIds\":[]}")))
            @Valid @RequestBody DepartmentDto input) {
        return departmentService.updateOne(departmentId, input);
    }

    @DeleteMapping("/{departmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete an empty department")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Department deleted; response has no body"),
            @ApiResponse(responseCode = "400", description = "Department ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Department does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Department has courses or the ID is not positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public void deleteOne(
            @Parameter(name = "departmentId", description = "Positive department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId) {
        departmentService.deleteOne(departmentId);
    }

    @GetMapping("/{departmentId}/courses")
    @Operation(summary = "List a department's courses")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Courses returned",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CourseDto.class)))),
            @ApiResponse(responseCode = "400", description = "Department ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Department does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Department ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public List<CourseDto> getCourses(
            @Parameter(name = "departmentId", description = "Positive department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId) {
        return departmentService.getCourses(departmentId);
    }

    @PostMapping("/{departmentId}/courses")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a course in a department",
            description = "Creates a course with no student registrations. The body departmentId must match the path departmentId.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Course created",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDto.class))),
            @ApiResponse(responseCode = "400", description = "Path ID is not an integer or request body fails DTO validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Department does not exist",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Department ID mismatch, generated ID supplied, or studentIds is not empty",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public CourseDto createCourse(
            @Parameter(name = "departmentId", description = "Positive owning department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Course fields; departmentId must match the path, the generated ID is omitted, and studentIds is empty",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CourseDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":null,\"code\":\"CS101\",\"title\":\"Introduction to Computing\",\"tuition\":125.5,\"active\":true,\"level\":\"INTRODUCTORY\",\"departmentId\":1,\"studentIds\":[]}")))
            @Valid @RequestBody CourseDto input) {
        requireCourseDepartmentId(departmentId, input);
        return departmentService.createCourse(input);
    }

    @GetMapping("/{departmentId}/courses/{courseId}")
    @Operation(summary = "Get a course in a department")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDto.class))),
            @ApiResponse(responseCode = "400", description = "A path ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Department or course does not exist, or course is not in this department",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "A path ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public CourseDto getCourse(
            @Parameter(name = "departmentId", description = "Positive owning department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId,
            @Parameter(name = "courseId", description = "Positive course identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("courseId") int courseId) {
        return departmentService.getCourse(departmentId, courseId);
    }

    @PutMapping("/{departmentId}/courses/{courseId}")
    @Operation(summary = "Update a course or move it to another department",
            description = "Updates course fields and may change departmentId. The course must currently belong to the path department; studentIds must remain unchanged.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Course updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CourseDto.class))),
            @ApiResponse(responseCode = "400", description = "A path ID is not an integer or request body fails DTO validation",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Current department, course, or target department does not exist; or course is not in path department",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Path/body ID conflict, or student registrations changed in this operation",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public CourseDto updateCourse(
            @Parameter(name = "departmentId", description = "Positive current owning department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId,
            @Parameter(name = "courseId", description = "Positive course identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("courseId") int courseId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Replacement course fields; studentIds must equal the current registrations. departmentId may move the course.",
                    required = true,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CourseDto.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"id\":1,\"code\":\"CS101\",\"title\":\"Introduction to Computing\",\"tuition\":125.5,\"active\":true,\"level\":\"INTRODUCTORY\",\"departmentId\":1,\"studentIds\":[]}")))
            @Valid @RequestBody CourseDto input) {
        return departmentService.updateCourse(departmentId, courseId, input);
    }

    @DeleteMapping("/{departmentId}/courses/{courseId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a course with no registered students")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Course deleted; response has no body"),
            @ApiResponse(responseCode = "400", description = "A path ID is not a valid integer",
                    content = @Content(mediaType = "application/problem+json", schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Department or course does not exist, or course is not in this department",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Request resource not found"))),
            @ApiResponse(responseCode = "409", description = "Course has registered students or a path ID must be positive",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Internal conflict happened."))),
            @ApiResponse(responseCode = "500", description = "Unexpected server error", content = @Content(mediaType = "application/json"))
    })
    public void deleteCourse(
            @Parameter(name = "departmentId", description = "Positive owning department identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("departmentId") int departmentId,
            @Parameter(name = "courseId", description = "Positive course identifier", required = true, example = "1",
                    schema = @Schema(type = "integer", minimum = "1"))
            @PathVariable("courseId") int courseId) {
        departmentService.deleteCourse(departmentId, courseId);
    }

    private static void requireCourseDepartmentId(int departmentId, CourseDto input) {
        if (input.departmentId() != departmentId) {
            throw new IllegalArgumentException("Course departmentId must match the requested department");
        }
    }
}
