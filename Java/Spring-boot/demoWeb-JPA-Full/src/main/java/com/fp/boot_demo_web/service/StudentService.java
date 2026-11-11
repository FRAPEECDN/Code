package com.fp.boot_demo_web.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fp.boot_demo_web.exceptions.EntityNotFoundException;
import com.fp.boot_demo_web.model.dto.StudentDto;
import com.fp.boot_demo_web.model.dto.StudentProfileDto;
import com.fp.boot_demo_web.model.entities.CourseEntity;
import com.fp.boot_demo_web.model.entities.StudentEntity;
import com.fp.boot_demo_web.model.entities.StudentProfileEntity;
import com.fp.boot_demo_web.repository.CourseRepository;
import com.fp.boot_demo_web.repository.StudentProfileRepository;
import com.fp.boot_demo_web.repository.StudentRepository;

import lombok.RequiredArgsConstructor;

/** Provides transactional student, course-registration, and profile operations. */
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final StudentProfileRepository studentProfileRepository;

    /** Retrieves all students, including their current course and profile IDs. */
    @Transactional(readOnly = true)
    public List<StudentDto> getAll() {
        return studentRepository.findAll().stream()
                .map(this::toStudentDto)
                .toList();
    }

    /** Retrieves one student by ID. */
    @Transactional(readOnly = true)
    public StudentDto getOne(int id) {
        return toStudentDto(requireStudent(id));
    }

    /**
     * Creates a student and optionally registers the student for existing courses.
     * A profile must be created separately with {@link #createProfile(StudentProfileDto)}.
     */
    @Transactional
    public StudentDto createOne(StudentDto input) {
        Objects.requireNonNull(input, "Student input must not be null");
        requireNewId(input.id(), "Student");
        if (input.profileId() != null) {
            throw new IllegalArgumentException("Create a student profile through createProfile");
        }

        Set<CourseEntity> courses = resolveCourses(input.courseIds());
        StudentEntity student = StudentEntity.builder()
                .name(input.name())
                .academicYear(input.academicYear())
                .active(input.active())
                .courses(courses)
                .build();
        courses.forEach(course -> course.getStudents().add(student));
        return toStudentDto(studentRepository.save(student));
    }

    /**
     * Replaces the student's scalar fields and current course registrations.
     * The profile association is managed through the profile methods.
     */
    @Transactional
    public StudentDto updateOne(int id, StudentDto input) {
        Objects.requireNonNull(input, "Student input must not be null");
        requireMatchingId(id, input.id(), "Student");

        StudentEntity student = requireStudent(id);
        Integer currentProfileId = student.getProfile() == null ? null : student.getProfile().getId();
        if (input.profileId() != null && !Objects.equals(input.profileId(), currentProfileId)) {
            throw new IllegalArgumentException("Update the student's profile through the profile methods");
        }

        Set<CourseEntity> courses = resolveCourses(input.courseIds());
        student.setName(input.name());
        student.setAcademicYear(input.academicYear());
        student.setActive(input.active());
        replaceCourses(student, courses);
        return toStudentDto(studentRepository.save(student));
    }

    /** Deletes a student and its profile and current course registrations. */
    @Transactional
    public void deleteOne(int id) {
        StudentEntity student = requireStudent(id);
        new HashSet<>(student.getCourses()).forEach(course -> course.getStudents().remove(student));
        student.getCourses().clear();
        studentRepository.delete(student);
    }

    /**
     * Registers a student for a course. Registrations represent the student's
     * current courses; the model does not retain prior-year enrollment history.
     */
    @Transactional
    public StudentDto registerForCourse(int studentId, int courseId) {
        StudentEntity student = requireStudent(studentId);
        CourseEntity course = requireCourse(courseId);
        if (student.getCourses().add(course)) {
            course.getStudents().add(student);
        }
        return toStudentDto(studentRepository.save(student));
    }

    /** Deregisters a student from a course; repeating the operation is harmless. */
    @Transactional
    public StudentDto deregisterFromCourse(int studentId, int courseId) {
        StudentEntity student = requireStudent(studentId);
        CourseEntity course = requireCourse(courseId);
        if (student.getCourses().remove(course)) {
            course.getStudents().remove(student);
        }
        return toStudentDto(studentRepository.save(student));
    }

    /** Retrieves all student profiles. */
    @Transactional(readOnly = true)
    public List<StudentProfileDto> getAllProfiles() {
        return studentProfileRepository.findAll().stream()
                .map(this::toStudentProfileDto)
                .toList();
    }

    /** Retrieves one profile by its ID. */
    @Transactional(readOnly = true)
    public StudentProfileDto getProfile(int profileId) {
        return toStudentProfileDto(requireProfile(profileId));
    }

    /** Retrieves the profile for a student. */
    @Transactional(readOnly = true)
    public StudentProfileDto getProfileForStudent(int studentId) {
        requireStudent(studentId);
        return studentProfileRepository.findByStudent_Id(studentId)
                .map(this::toStudentProfileDto)
                .orElseThrow(EntityNotFoundException::new);
    }

    /** Creates the optional profile for an existing student. */
    @Transactional
    public StudentProfileDto createProfile(StudentProfileDto input) {
        Objects.requireNonNull(input, "Profile input must not be null");
        requireNewId(input.id(), "Student profile");
        StudentEntity student = requireStudent(input.studentId());
        if (student.getProfile() != null || studentProfileRepository.findByStudent_Id(student.getId()).isPresent()) {
            throw new IllegalStateException("The student already has a profile");
        }

        StudentProfileEntity profile = StudentProfileEntity.builder()
                .email(input.email())
                .phone(input.phone())
                .student(student)
                .build();
        student.setProfile(profile);
        return toStudentProfileDto(studentProfileRepository.save(profile));
    }

    /** Updates a profile without changing its owning student. */
    @Transactional
    public StudentProfileDto updateProfile(int profileId, StudentProfileDto input) {
        Objects.requireNonNull(input, "Profile input must not be null");
        return updateProfile(requireProfile(profileId), input);
    }

    /** Updates a student's profile without a separate profile lookup transaction. */
    @Transactional
    public StudentProfileDto updateProfileForStudent(int studentId, StudentProfileDto input) {
        Objects.requireNonNull(input, "Profile input must not be null");
        requireStudent(studentId);
        StudentProfileEntity profile = studentProfileRepository.findByStudent_Id(studentId)
                .orElseThrow(EntityNotFoundException::new);
        return updateProfile(profile, input);
    }

    private StudentProfileDto updateProfile(StudentProfileEntity profile, StudentProfileDto input) {
        requireMatchingId(profile.getId(), input.id(), "Student profile");
        if (!Objects.equals(input.studentId(), profile.getStudent().getId())) {
            throw new IllegalArgumentException("A profile cannot be reassigned to another student");
        }
        profile.setEmail(input.email());
        profile.setPhone(input.phone());
        return toStudentProfileDto(studentProfileRepository.save(profile));
    }

    /** Deletes a profile while preserving its student. */
    @Transactional
    public void deleteProfile(int profileId) {
        deleteProfile(requireProfile(profileId));
    }

    /** Deletes the profile for a student without a separate profile lookup transaction. */
    @Transactional
    public void deleteProfileForStudent(int studentId) {
        requireStudent(studentId);
        StudentProfileEntity profile = studentProfileRepository.findByStudent_Id(studentId)
                .orElseThrow(EntityNotFoundException::new);
        deleteProfile(profile);
    }

    private void deleteProfile(StudentProfileEntity profile) {
        profile.getStudent().setProfile(null);
        studentProfileRepository.delete(profile);
    }

    private StudentEntity requireStudent(int id) {
        return studentRepository.findById(requirePositiveId(id, "Student"))
                .orElseThrow(EntityNotFoundException::new);
    }

    private CourseEntity requireCourse(int id) {
        return courseRepository.findById(requirePositiveId(id, "Course"))
                .orElseThrow(EntityNotFoundException::new);
    }

    private StudentProfileEntity requireProfile(int id) {
        return studentProfileRepository.findById(requirePositiveId(id, "Student profile"))
                .orElseThrow(EntityNotFoundException::new);
    }

    private Set<CourseEntity> resolveCourses(Set<Integer> courseIds) {
        Set<Integer> ids = requireIds(courseIds, "courseIds");
        Map<Integer, CourseEntity> coursesById = new HashMap<>();
        courseRepository.findAllById(ids).forEach(course -> coursesById.put(course.getId(), course));
        if (coursesById.size() != ids.size()) {
            throw new EntityNotFoundException();
        }

        Set<CourseEntity> courses = new LinkedHashSet<>();
        ids.forEach(id -> courses.add(coursesById.get(id)));
        return courses;
    }

    private void replaceCourses(StudentEntity student, Set<CourseEntity> courses) {
        Set<CourseEntity> previousCourses = new HashSet<>(student.getCourses());
        previousCourses.forEach(course -> course.getStudents().remove(student));
        student.getCourses().clear();
        courses.forEach(course -> {
            student.getCourses().add(course);
            course.getStudents().add(student);
        });
    }

    private StudentDto toStudentDto(StudentEntity student) {
        Set<Integer> courseIds = new TreeSet<>();
        student.getCourses().forEach(course -> courseIds.add(course.getId()));
        Integer profileId = student.getProfile() == null ? null : student.getProfile().getId();
        return new StudentDto(student.getId(), student.getName(), student.getAcademicYear(),
                student.getActive(), profileId, courseIds);
    }

    private StudentProfileDto toStudentProfileDto(StudentProfileEntity profile) {
        return new StudentProfileDto(profile.getId(), profile.getEmail(), profile.getPhone(),
                profile.getStudent().getId());
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

    private static int requirePositiveId(int id, String entityName) {
        if (id <= 0) {
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
