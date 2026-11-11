package com.fp.boot_demo_web.model.entities;

import java.util.HashSet;
import java.util.Set;

import com.fp.boot_demo_web.model.enums.CourseLevel;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A department-owned course with scalar, ordinal-enum, and student relationships. */
@Entity
@Table(name = "course")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false, length = 20, unique = true)
    @NotBlank
    @Size(max = 20)
    private String code;

    @Column(name = "title", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String title;

    @Column(name = "tuition", nullable = false)
    @NotNull
    @PositiveOrZero
    private Double tuition;

    @Column(name = "active", nullable = false)
    @NotNull
    private Boolean active;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "course_level", nullable = false)
    @NotNull
    private CourseLevel level;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    @NotNull
    private DepartmentEntity department;

    @Builder.Default
    @ManyToMany(mappedBy = "courses")
    private Set<StudentEntity> students = new HashSet<>();
}
