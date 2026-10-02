# Foundation POJO

A small department and project model implemented in three Java-version variants. The modules share the same domain API and console application, and target Java 17, 21, and 25 respectively.

## Project Layout

- `foundations-pojo_17/`: Java 17 source, tests, and runnable application.
- `foundations-pojo_21/`: Java 21 source, tests, and runnable application.
- `foundations-pojo_25/`: Java 25 source, tests, and runnable application.
- Each module uses package `com.fp.coding`; `PojoModelTest` exercises model validation, project lifecycle, payroll, and department relationships.
- The parent `pom.xml` builds all modules and configures Maven Compiler, JUnit, Lombok, JaCoCo, and Javadoc.

## Domain Model

| Type | Responsibility |
| --- | --- |
| `Information` | Common contract for returning a concise `summary()` string. |
| `Staff` | Sealed base type for department personnel. Normalizes names, validates non-negative finite salary, and provides `roleTitle()` and `summary()`. |
| `Manager` | Staff role with a non-negative team size. |
| `Developer` | Staff role with a required specialty. |
| `ProjectOwner` | Staff role that keeps a read-only view of projects registered to that owner. Projects register themselves with their owner when created. |
| `Department` | Requires a name, manager, and project owner. Tracks developers with `StaffLevel` and `StaffContent`, department projects, staff-to-project assignments, and annual payroll. Collection getters return snapshots. |
| `Department.DeveloperAssignment` | Holds a developer's department position and happiness rating. |
| `Department.ProjectAssignment` | Connects one current department staff member to one current department project. |
| `Project` | Stores owner, name, sponsor, budget, dates, current status, and status history. It validates required text, finite non-negative budget, and deadline ordering. |
| `ProjectStatus` | Defines project lifecycle states and the allowed next-state transitions. |
| `StaffLevel` | Defines JUNIOR, MID, SENIOR, and LEAD levels with numeric ranks. |
| `StaffContent` | Defines HAPPY, NEUTRAL, and SAD values and their text labels. |
| `NameValidation` | Utility that trims and validates names used by staff. |
| `Main` | Starts the in-memory interactive management application. |
| `ConsoleContext` | Holds the session's directories and shared input/selection helpers. |
| `DepartmentConsole` | Creates, lists, and removes departments, and adds existing developers and projects during setup. |
| `StaffConsole` | Creates staff-directory entries and adds/removes developers in departments. |
| `ProjectConsole` | Creates projects and adds/removes them from departments. |
| `AssignmentConsole` | Assigns and unassigns department projects to department staff. |

## Run

Requires Maven 3.6.3 or newer and a JDK 25 installation to build the full reactor. Run the full test and quality-gate suite from the repository root:

```powershell
mvn test
```

Run one version's console application after compiling that module:

```powershell
mvn -pl foundations-pojo_17 test
java -cp foundations-pojo_17\target\classes com.fp.coding.Main
```

Use `foundations-pojo_21` or `foundations-pojo_25` in place of `foundations-pojo_17` to launch the other variants. The application uses only in-memory collections; exiting discards the session data.

## Console Workflow

1. In **Staff**, create a manager and a project owner, then create any developers.
2. In **Projects**, create projects and select an existing project owner.
3. In **Departments**, add a department by choosing an existing manager and project owner. You can attach existing developers and projects owned by that selected project owner.
4. In **Staff**, add or remove developers from a department. A developer can be shared across departments.
5. In **Projects → Assignments**, assign or unassign a department project to a department staff member. Removing a developer or removing a project from a department also clears its affected assignments.

A department always requires its manager and project owner, so those two structural roles cannot be removed from an existing department. Projects removed from a department remain in the in-memory project directory and in their project owner's registry; removal only drops that department's link. Department projects must have the same owner as the department.

## Model Behavior

`Department.addDeveloper` and `Department.addProject` avoid adding the same object twice. `removeStaff` supports removable developers and cleans up their project assignments; the required manager and project owner remain. `assignProject` accepts only staff and projects already held by that department, prevents duplicate object-pair assignments, and `unassignProject` removes a matching pair. Returned department and owner collections are immutable snapshots.

A project's initial state is `REGISTERED`. The permitted transitions are `REGISTERED → PLANNED → IMPLEMENTATING → FINISHED`, with cancellation permitted from `REGISTERED`, `PLANNED`, and `IMPLEMENTATING`. Transition dates cannot move backward; terminal states cannot transition further.
