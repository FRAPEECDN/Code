package com.fp.coding;

import io.arxila.javatuples.Decet;
import io.arxila.javatuples.Empty;
import io.arxila.javatuples.KeyValue;
import io.arxila.javatuples.LabelValue;
import io.arxila.javatuples.MapEntry;
import io.arxila.javatuples.Octet;
import io.arxila.javatuples.Pair;
import io.arxila.javatuples.Quartet;
import io.arxila.javatuples.Quintet;
import io.arxila.javatuples.Septet;
import io.arxila.javatuples.Sextet;
import io.arxila.javatuples.Solo;
import io.arxila.javatuples.Trio;
import io.arxila.javatuples.Tuple;
import io.arxila.javatuples.Tuple0;
import io.arxila.javatuples.Tuple1;
import io.arxila.javatuples.Tuple10;
import io.arxila.javatuples.Tuple2;
import io.arxila.javatuples.Tuple3;
import io.arxila.javatuples.Tuple4;
import io.arxila.javatuples.Tuple5;
import io.arxila.javatuples.Tuple6;
import io.arxila.javatuples.Tuple7;
import io.arxila.javatuples.Tuple8;
import io.arxila.javatuples.Tuple9;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/** Demonstrates JavaTuples arities and key/value specializations with project data. */
public final class JavaTuplesDemo {
    private JavaTuplesDemo() {}

    /**
     * Runs tuple construction, access, and immutable derivation examples.
     *
     * @param project project used as tuple data
     * @param versionLabel Java version label for the sample tuple
     */
    public static void run(Project project, String versionLabel) {
        System.out.println("\nJavaTuples: Tuple0 through Tuple10 and named forms");

        String name = project.getProjectName();
        String sponsor = project.getSponsor();
        ProjectStatus status = project.getStatus();
        Double budget = project.getBudget();
        LocalDate startDate = project.getStartDate();
        LocalDate deadline = project.getDeadline();
        Integer ownerProjectCount = project.getProjectOwner().getProjects().size();
        Map<ProjectStatus, LocalDate> history = project.getStatusHistory();
        String sampleLabel = versionLabel + " sample";

        List<Tuple> tuples = List.of(
                Tuple0.of(), Tuple1.of(name), Tuple2.of(name, status),
                Tuple3.of(name, status, budget), Tuple4.of(name, sponsor, status, budget),
                Tuple5.of(name, sponsor, status, budget, deadline),
                Tuple6.of(name, sponsor, status, budget, startDate, deadline),
                Tuple7.of(name, sponsor, status, budget, startDate, deadline, ownerProjectCount),
                Tuple8.of(name, sponsor, status, budget, startDate, deadline, ownerProjectCount, history),
                Tuple9.of(name, sponsor, status, budget, startDate, deadline, ownerProjectCount, history,
                        CollectionDemoSupport.REGISTERED_ON),
                Tuple10.of(name, sponsor, status, budget, startDate, deadline, ownerProjectCount, history,
                        ownerProjectCount, sampleLabel),
                Empty.of(), Solo.of(name), Pair.of(name, status), Trio.of(name, status, budget),
                Quartet.of(name, sponsor, status, budget), Quintet.of(name, sponsor, status, budget, deadline),
                Sextet.of(name, sponsor, status, budget, startDate, deadline),
                Septet.of(name, sponsor, status, budget, startDate, deadline, ownerProjectCount),
                Octet.of(name, sponsor, status, budget, startDate, deadline, ownerProjectCount, history),
                Decet.of(name, sponsor, status, budget, startDate, deadline, ownerProjectCount, history,
                        ownerProjectCount, sampleLabel));

        tuples.forEach(tuple -> System.out.println(tuple.getClass().getSimpleName()
                + " size=" + tuple.size() + " values=" + tuple.values()));
        System.out.println("KeyValue: " + KeyValue.of(name, status));
        System.out.println("LabelValue: " + LabelValue.of("project", name));
        System.out.println("MapEntry: " + MapEntry.of(name, budget));

        // Tuples are immutable; withValue1 returns a new Pair and leaves the original unchanged.
        Pair<String, ProjectStatus> statusPair = Pair.of(name, status);
        Pair<String, ProjectStatus> changedPair = statusPair.withValue1(ProjectStatus.FINISHED);
        System.out.println("Tuple access/update: " + statusPair.value(0) + " -> " + changedPair
                + "; original remains " + statusPair);
    }
}
