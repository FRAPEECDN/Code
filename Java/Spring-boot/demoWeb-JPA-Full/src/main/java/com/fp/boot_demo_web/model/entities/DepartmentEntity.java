package com.fp.boot_demo_web.model.entities;

import java.util.HashSet;
import java.util.Set;

import com.fp.boot_demo_web.model.enums.DepartmentType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A department that owns courses and stores its type as an enum name. */
@Entity
@Table(name = "department")
@Builder(toBuilder = true)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    @NotBlank
    @Size(max = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "department_type", nullable = false, length = 32)
    @NotNull
    private DepartmentType type;

    @Builder.Default
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CourseEntity> courses = new HashSet<>();
}
