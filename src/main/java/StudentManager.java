import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages a collection of students and provides search and ranking operations.
 */
public class StudentManager {

    private final List<Student> students = new ArrayList<>();
    private final Map<Integer, Student> studentsById = new HashMap<>();

    /**
     * Creates an empty student manager.
     */
    public StudentManager() {
    }

    /**
     * Creates a manager initialized with the given students.
     *
     * @param initialStudents the students to add
     */
    public StudentManager(Collection<Student> initialStudents) {
        if (initialStudents != null) {
            initialStudents.forEach(this::addStudent);
        }
    }

    /**
     * Adds a student to the manager.
     *
     * @param student the student to add
     * @throws IllegalArgumentException if the student is null or the ID already exists
     */
    public void addStudent(Student student) {
        Objects.requireNonNull(student, "Student must not be null.");
        if (studentsById.containsKey(student.getId())) {
            throw new IllegalArgumentException("A student with ID " + student.getId() + " already exists.");
        }

        students.add(student);
        studentsById.put(student.getId(), student);
    }

    /**
     * Returns an unmodifiable list of managed students.
     *
     * @return the student list
     */
    public List<Student> getAllStudents() {
        return Collections.unmodifiableList(students);
    }

    /**
     * Finds the student with the highest average.
     *
     * @return an optional containing the topper student
     */
    public Optional<Student> getTopper() {
        return students.stream().max(Comparator.comparingDouble(Student::getAverage));
    }

    /**
     * Returns the students who share the highest average.
     *
     * @return the topper students, or an empty list if no students exist
     */
    public List<Student> getToppers() {
        if (students.isEmpty()) {
            return Collections.emptyList();
        }

        double highestAverage = students.stream()
                .mapToDouble(Student::getAverage)
                .max()
                .orElse(0.0);

        return students.stream()
                .filter(student -> Math.abs(student.getAverage() - highestAverage) < 1e-6)
                .collect(Collectors.toList());
    }

    /**
     * Finds a student by their ID.
     *
     * @param id the ID to search for
     * @return an optional containing the student if found
     */
    public Optional<Student> findStudentById(int id) {
        return Optional.ofNullable(studentsById.get(id));
    }

    /**
     * Returns students whose average is below the provided threshold.
     *
     * @param threshold the passing threshold
     * @return a list of failing students
     */
    public List<Student> getFailingStudents(int threshold) {
        return students.stream()
                .filter(student -> student.getAverage() < threshold)
                .collect(Collectors.toList());
    }

    /**
     * Removes a student by their ID.
     *
     * @param id the student ID
     * @return true if the student was removed, false otherwise
     */
    public boolean removeStudentById(int id) {
        Student removed = studentsById.remove(id);
        if (removed == null) {
            return false;
        }
        students.removeIf(student -> student.getId() == id);
        return true;
    }

    /**
     * Updates the marks for an existing student.
     *
     * @param id the student ID
     * @param newMarks the new marks to assign
     * @throws IllegalArgumentException if the student does not exist or newMarks is null
     */
    public void updateStudentMarks(int id, List<Integer> newMarks) {
        Objects.requireNonNull(newMarks, "Marks must not be null.");

        Student existing = findStudentById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student with ID " + id + " does not exist."));

        Student updated = new Student(existing.getId(), existing.getName(), newMarks);
        int index = students.indexOf(existing);
        students.set(index, updated);
        studentsById.put(id, updated);
    }
}
