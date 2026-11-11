package com.fp.boot_demo_web.service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fp.boot_demo_web.exceptions.EntityNotFoundException;
import com.fp.boot_demo_web.model.dto.CourseDto;
import com.fp.boot_demo_web.model.dto.DepartmentDto;
import com.fp.boot_demo_web.model.entities.CourseEntity;
import com.fp.boot_demo_web.model.entities.DepartmentEntity;
import com.fp.boot_demo_web.repository.CourseRepository;
import com.fp.boot_demo_web.repository.DepartmentRepository;

import lombok.RequiredArgsConstructor;

/** Provides transactional department and course management operations. */
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;

    /** Retrieves all departments and their course IDs. */
    @Transactional(readOnly = true)
    public List<DepartmentDto> getAll() {
        return departmentRepository.findAll().stream()
                .map(this::toDepartmentDto)
                .toList();
    }

    /** Retrieves one department by ID. */
    @Transactional(readOnly = true)
    public DepartmentDto getOne(int id) {
        return toDepartmentDto(requireDepartment(id));
    }

    /** Creates an empty department; courses are managed through course operations. */
    @Transactional
    public DepartmentDto createOne(DepartmentDto input) {
        Objects.requireNonNull(input, "Department input must not be null");
        requireNewId(input.id(), "Department");
        if (!requireIds(input.courseIds(), "courseIds").isEmpty()) {
            throw new IllegalArgumentException("Create department courses through createCourse");
        }

        DepartmentEntity department = DepartmentEntity.builder()
                .name(input.name())
                .type(input.type())
                .build();
        return toDepartmentDto(departmentRepository.save(department));
    }

    /** Updates department details without changing its course assignments. */
    @Transactional
    public DepartmentDto updateOne(int id, DepartmentDto input) {
        Objects.requireNonNull(input, "Department input must not be null");
        requireMatchingId(id, input.id(), "Department");

        DepartmentEntity department = requireDepartment(id);
        Set<Integer> requestedCourseIds = requireIds(input.courseIds(), "courseIds");
        if (!requestedCourseIds.equals(courseIdsForDepartment(id))) {
            throw new IllegalArgumentException("Manage department courses through the course operations");
        }
        department.setName(input.name());
        department.setType(input.type());
        return toDepartmentDto(departmentRepository.save(department));
    }

    /** Deletes a department only when it has no courses. */
    @Transactional
    public void deleteOne(int id) {
        DepartmentEntity department = requireDepartment(id);
        if (!courseRepository.findByDepartment_Id(id).isEmpty()) {
            throw new IllegalStateException("A department with courses cannot be deleted");
        }
        departmentRepository.delete(department);
    }

    /** Lists courses owned by a department. */
    @Transactional(readOnly = true)
    public List<CourseDto> getCourses(int departmentId) {
        requireDepartment(departmentId);
        return courseRepository.findByDepartment_Id(departmentId).stream()
                .map(this::toCourseDto)
                .toList();
    }

    /** Retrieves a course only if it belongs to the requested department. */
    @Transactional(readOnly = true)
    public CourseDto getCourse(int departmentId, int courseId) {
        requireDepartment(departmentId);
        CourseEntity course = requireCourse(courseId);
        if (!course.getDepartment().getId().equals(departmentId)) {
            throw new EntityNotFoundException();
        }
        return toCourseDto(course);
    }

    /** Creates a course for its referenced department with no student registrations. */
    @Transactional
    public CourseDto createCourse(CourseDto input) {
        Objects.requireNonNull(input, "Course input must not be null");
        requireNewId(input.id(), "Course");
        if (!requireIds(input.studentIds(), "studentIds").isEmpty()) {
            throw new IllegalArgumentException("Register students through StudentService");
        }
        DepartmentEntity department = requireDepartment(requirePositiveId(input.departmentId(), "Department"));
        CourseEntity course = CourseEntity.builder()
                .code(input.code())
                .title(input.title())
                .tuition(input.tuition())
                .active(input.active())
                .level(input.level())
                .department(department)
                .build();
        CourseEntity savedCourse = courseRepository.save(course);
        department.getCourses().add(savedCourse);
        return toCourseDto(savedCourse);
    }

    /** Updates course details and department without changing student registrations. */
    @Transactional
    public CourseDto updateCourse(int courseId, CourseDto input) {
        return updateCourse(requireCourse(courseId), input);
    }

    /**
     * Updates a course only if it currently belongs to the supplied department.
     * The input department ID may move it to another department.
     */
    @Transactional
    public CourseDto updateCourse(int departmentId, int courseId, CourseDto input) {
        requireDepartment(departmentId);
        CourseEntity course = requireCourse(courseId);
        if (!course.getDepartment().getId().equals(departmentId)) {
            throw new EntityNotFoundException();
        }
        return updateCourse(course, input);
    }

    private CourseDto updateCourse(CourseEntity course, CourseDto input) {
        Objects.requireNonNull(input, "Course input must not be null");
        requireMatchingId(course.getId(), input.id(), "Course");

        Set<Integer> requestedStudentIds = requireIds(input.studentIds(), "studentIds");
        if (!requestedStudentIds.equals(studentIdsForCourse(course))) {
            throw new IllegalArgumentException("Manage course registrations through StudentService");
        }
        DepartmentEntity newDepartment = requireDepartment(requirePositiveId(input.departmentId(), "Department"));
        DepartmentEntity oldDepartment = course.getDepartment();
        if (!oldDepartment.getId().equals(newDepartment.getId())) {
            oldDepartment.getCourses().remove(course);
            newDepartment.getCourses().add(course);
            course.setDepartment(newDepartment);
        }
        course.setCode(input.code());
        course.setTitle(input.title());
        course.setTuition(input.tuition());
        course.setActive(input.active());
        course.setLevel(input.level());
        return toCourseDto(courseRepository.save(course));
    }

    /** Deletes a course only when no students are registered for it. */
    @Transactional
    public void deleteCourse(int courseId) {
        deleteCourse(requireCourse(courseId));
    }

    /** Deletes a course only if it belongs to the supplied department and has no registrations. */
    @Transactional
    public void deleteCourse(int departmentId, int courseId) {
        requireDepartment(departmentId);
        CourseEntity course = requireCourse(courseId);
        if (!course.getDepartment().getId().equals(departmentId)) {
            throw new EntityNotFoundException();
        }
        deleteCourse(course);
    }

    private void deleteCourse(CourseEntity course) {
        if (!course.getStudents().isEmpty()) {
            throw new IllegalStateException("A course with registered students cannot be deleted");
        }
        course.getDepartment().getCourses().remove(course);
        courseRepository.delete(course);
    }

    private DepartmentEntity requireDepartment(int id) {
        return departmentRepository.findById(requirePositiveId(id, "Department"))
                .orElseThrow(EntityNotFoundException::new);
    }

    private CourseEntity requireCourse(int id) {
        return courseRepository.findById(requirePositiveId(id, "Course"))
                .orElseThrow(EntityNotFoundException::new);
    }

    private Set<Integer> courseIdsForDepartment(int departmentId) {
        Set<Integer> ids = new TreeSet<>();
        courseRepository.findByDepartment_Id(departmentId).forEach(course -> ids.add(course.getId()));
        return ids;
    }

    private Set<Integer> studentIdsForCourse(CourseEntity course) {
        Set<Integer> ids = new TreeSet<>();
        course.getStudents().forEach(student -> ids.add(student.getId()));
        return ids;
    }

    private DepartmentDto toDepartmentDto(DepartmentEntity department) {
        Set<Integer> courseIds = new TreeSet<>();
        department.getCourses().forEach(course -> courseIds.add(course.getId()));
        return new DepartmentDto(department.getId(), department.getName(), department.getType(), courseIds);
    }

    private CourseDto toCourseDto(CourseEntity course) {
        Set<Integer> studentIds = new TreeSet<>();
        course.getStudents().forEach(student -> studentIds.add(student.getId()));
        return new CourseDto(course.getId(), course.getCode(), course.getTitle(), course.getTuition(),
                course.getActive(), course.getLevel(), course.getDepartment().getId(), studentIds);
    }

    private static Set<Integer> requireIds(Set<Integer> ids, String fieldName) {
        if (ids == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        Set<Integer> validIds = new TreeSet<>();
        for (Integer id : ids) {
            if (id == null || id <= 0) {
                throw new IllegalArgumentException(fieldName + " must contain only positive IDs");
            }
            validIds.add(id);
        }
        return validIds;
    }

    private static int requirePositiveId(Integer id, String entityName) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(entityName + " ID must be positive");
        }
        return id;
    }

    private static void requireNewId(Integer id, String entityName) {
        if (id != null) {
            throw new IllegalArgumentException(entityName + " ID is generated and must be omitted when creating");
        }
    }

    private static void requireMatchingId(int pathId, Integer dtoId, String entityName) {
        if (dtoId != null && dtoId != pathId) {
            throw new IllegalArgumentException(entityName + " ID must match the requested resource");
        }
    }
}
