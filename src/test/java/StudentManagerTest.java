import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StudentManagerTest {

    @Test
    void studentConstructor_clampsOutOfRangeMarks() {
        Student student = new Student(101, "Alice", List.of(120, -5, 85));

        assertEquals(List.of(100, 0, 85), student.getMarks());
    }

    @Test
    void studentAverageAndGradeAreCalculatedCorrectly() {
        Student student = new Student(101, "Bob", List.of(90, 80, 70));

        assertEquals(80.0, student.getAverage(), 1e-12);
        assertEquals('B', student.getGrade());
    }

    @Test
    void addStudent_addsStudentsAndReturnsAllStudents() {
        StudentManager manager = new StudentManager();
        Student alice = new Student(101, "Alice", List.of(85, 90, 78));
        Student bob = new Student(102, "Bob", List.of(72, 68, 80));

        manager.addStudent(alice);
        manager.addStudent(bob);

        List<Student> allStudents = manager.getAllStudents();

        assertEquals(2, allStudents.size());
        assertTrue(allStudents.contains(alice));
        assertTrue(allStudents.contains(bob));
    }

    @Test
    void addStudent_duplicateId_throwsException() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new Student(101, "Alice", List.of(85, 90, 78)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> manager.addStudent(new Student(101, "Alicia", List.of(80, 80, 80))));

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void getToppers_returnsAllStudentsWithHighestAverage() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new Student(101, "Alice", List.of(90, 90, 90)));
        manager.addStudent(new Student(102, "Bob", List.of(90, 90, 90)));
        manager.addStudent(new Student(103, "Charlie", List.of(85, 85, 85)));

        List<Student> toppers = manager.getToppers();

        assertEquals(2, toppers.size());
        assertTrue(toppers.stream().anyMatch(student -> student.getName().equals("Alice")));
        assertTrue(toppers.stream().anyMatch(student -> student.getName().equals("Bob")));
    }

    @Test
    void getFailingStudents_returnsBelowThreshold() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new Student(101, "Alice", List.of(50, 50, 50)));
        manager.addStudent(new Student(102, "Bob", List.of(70, 70, 70)));

        List<Student> failing = manager.getFailingStudents(60);

        assertEquals(1, failing.size());
        assertEquals("Alice", failing.get(0).getName());
    }

    @Test
    void removeStudentById_removesStudent() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new Student(101, "Alice", List.of(85, 90, 78)));

        assertTrue(manager.removeStudentById(101));
        assertTrue(manager.getAllStudents().isEmpty());
    }

    @Test
    void updateStudentMarks_replacesMarksAndAverage() {
        StudentManager manager = new StudentManager();
        manager.addStudent(new Student(101, "Alice", List.of(85, 90, 78)));

        manager.updateStudentMarks(101, List.of(100, 100, 100));
        Student updated = manager.findStudentById(101).orElseThrow();

        assertEquals(100.0, updated.getAverage(), 1e-12);
        assertEquals('A', updated.getGrade());
    }
}
