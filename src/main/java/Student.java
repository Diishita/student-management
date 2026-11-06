import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a student with an ID, name, and a set of marks.
 */
public class Student {

    private final int id;
    private final String name;
    private final List<Integer> marks;

    /**
     * Creates a new student instance.
     *
     * @param id the student identifier; must be positive
     * @param name the student name; must not be null or blank
     * @param marks the marks for the student; null becomes an empty list
     */
    public Student(int id, String name, List<Integer> marks) {
        if (id <= 0) {
            throw new IllegalArgumentException("Student id must be positive.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Student name must not be blank.");
        }
        Objects.requireNonNull(marks, "Marks must not be null.");

        this.id = id;
        this.name = name;
        this.marks = List.copyOf(normalizeMarks(new ArrayList<>(marks)));
    }

    /**
     * Creates a new student instance from an array of marks.
     *
     * @param id the student identifier
     * @param name the student name
     * @param marks the marks for the student, null becomes an empty list
     */
    public Student(int id, String name, int[] marks) {
        this(id, name, convertArrayToList(marks));
    }

    private static List<Integer> convertArrayToList(int[] marks) {
        if (marks == null) {
            return new ArrayList<>();
        }
        List<Integer> list = new ArrayList<>(marks.length);
        for (int mark : marks) {
            list.add(mark);
        }
        return list;
    }

    private static List<Integer> normalizeMarks(List<Integer> marks) {
        for (int i = 0; i < marks.size(); i++) {
            int value = marks.get(i);
            if (value < 0) {
                marks.set(i, 0);
            } else if (value > 100) {
                marks.set(i, 100);
            }
        }
        return marks;
    }

    /**
     * Returns the student ID.
     *
     * @return the student identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the student name.
     *
     * @return the student name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a read-only view of the student's marks.
     *
     * @return the marks for the student
     */
    public List<Integer> getMarks() {
        return Collections.unmodifiableList(marks);
    }

    /**
     * Returns the average of the student's marks.
     *
     * @return the average score, or 0.0 when there are no marks
     */
    public double getAverage() {
        if (marks.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (int mark : marks) {
            total += mark;
        }
        return (double) total / marks.size();
    }

    /**
     * Returns the grade for the student based on the average.
     *
     * @return the letter grade from A to E
     */
    public char getGrade() {
        double average = getAverage();
        if (average >= 90) {
            return 'A';
        } else if (average >= 80) {
            return 'B';
        } else if (average >= 70) {
            return 'C';
        } else if (average >= 60) {
            return 'D';
        }
        return 'E';
    }

    /**
     * Returns the student's marks as a comma-separated string.
     *
     * @return formatted marks string
     */
    public String getMarksAsString() {
        return marks.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return String.format("Student{id=%d, name='%s', average=%.2f, grade=%s, marks=[%s]}",
                id, name, getAverage(), getGrade(), getMarksAsString());
    }
}
