package com.fp.coding;

import java.time.LocalDate;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Package-local factories and display helpers shared by collection demos. */
final class CollectionDemoSupport {
    static final LocalDate REGISTERED_ON = LocalDate.of(2026, 1, 10);

    private CollectionDemoSupport() {}

    static Project createStandaloneProject(String name, double budget) {
        LocalDate startDate = REGISTERED_ON.plusDays(1);
        return Project.builder()
                .projectOwner(new ProjectOwner())
                .projectName(name)
                .sponsor("Foundation")
                .budget(budget)
                .startDate(startDate)
                .deadline(startDate.plusMonths(6))
                .registeredOn(REGISTERED_ON)
                .build();
    }

    static List<String> projectNames(Collection<Project> projects) {
        return projects.stream().map(Project::getProjectName).collect(Collectors.toList());
    }

    static String projectName(Project project) {
        return project == null ? "not found" : project.getProjectName();
    }

    static Map<ProjectStatus, Long> groupSizes(Map<ProjectStatus, List<Project>> groups) {
        Map<ProjectStatus, Long> sizes = new EnumMap<>(ProjectStatus.class);
        groups.forEach((status, statusProjects) -> sizes.put(status, (long) statusProjects.size()));
        return sizes;
    }
}
