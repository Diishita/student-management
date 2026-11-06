import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * JavaFX GUI application for student management.
 */
public class Main extends Application {

    private final StudentManager manager = new StudentManager();
    private final ObservableList<Student> studentData = FXCollections.observableArrayList();

    private TextField idField;
    private TextField nameField;
    private TextField marksField;
    private TextField searchField;
    private TableView<Student> tableView;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Student Manager");

        initializeSampleStudents();
        BorderPane root = new BorderPane();

        root.setTop(createInputPanel());
        root.setCenter(createTable());
        root.setBottom(createActionPanel());

        Scene scene = new Scene(root, 800, 520);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshTable();
    }

    private void initializeSampleStudents() {
        manager.addStudent(new Student(101, "Alice", List.of(85, 90, 78)));
        manager.addStudent(new Student(102, "Bob", List.of(72, 68, 80)));
        manager.addStudent(new Student(103, "Charlie", List.of(95, 88, 91)));
    }

    private VBox createInputPanel() {
        Label header = new Label("Add / Update Student");
        header.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setPadding(new Insets(10));

        idField = new TextField();
        nameField = new TextField();
        marksField = new TextField();
        searchField = new TextField();

        form.add(new Label("ID:"), 0, 0);
        form.add(idField, 1, 0);
        form.add(new Label("Name:"), 0, 1);
        form.add(nameField, 1, 1);
        form.add(new Label("Marks (comma-separated):"), 0, 2);
        form.add(marksField, 1, 2);
        form.add(new Label("Search ID:"), 0, 3);
        form.add(searchField, 1, 3);

        Button addButton = new Button("Add Student");
        Button updateButton = new Button("Update Marks");
        Button searchButton = new Button("Search");

        addButton.setOnAction(event -> handleAddStudent());
        updateButton.setOnAction(event -> handleUpdateMarks());
        searchButton.setOnAction(event -> handleSearchById());

        HBox buttonBox = new HBox(10, addButton, updateButton, searchButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        VBox container = new VBox(5, header, form, buttonBox);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #f4f4f8;");
        return container;
    }

    private TableView<Student> createTable() {
        tableView = new TableView<>(studentData);

        TableColumn<Student, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(80);

        TableColumn<Student, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);

        TableColumn<Student, Double> averageColumn = new TableColumn<>("Average");
        averageColumn.setCellValueFactory(new PropertyValueFactory<>("average"));
        averageColumn.setPrefWidth(120);

        TableColumn<Student, Character> gradeColumn = new TableColumn<>("Grade");
        gradeColumn.setCellValueFactory(new PropertyValueFactory<>("grade"));
        gradeColumn.setPrefWidth(80);

        tableView.getColumns().addAll(idColumn, nameColumn, averageColumn, gradeColumn);
        tableView.setPlaceholder(new Label("No students available."));
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return tableView;
    }

    private HBox createActionPanel() {
        Button showToppersButton = new Button("Show Topper(s)");
        Button showFailingButton = new Button("Show Failing");
        Button removeButton = new Button("Remove Student");
        Button refreshButton = new Button("Refresh Table");

        showToppersButton.setOnAction(event -> handleShowToppers());
        showFailingButton.setOnAction(event -> handleShowFailing());
        removeButton.setOnAction(event -> handleRemoveStudent());
        refreshButton.setOnAction(event -> refreshTable());

        HBox actions = new HBox(10, showToppersButton, showFailingButton, removeButton, refreshButton);
        actions.setAlignment(Pos.CENTER);
        actions.setPadding(new Insets(10));
        actions.setStyle("-fx-background-color: #e8e8f0;");

        return actions;
    }

    private void handleAddStudent() {
        try {
            int id = parseIntegerField(idField, "ID");
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                showError("Name cannot be empty.");
                return;
            }

            List<Integer> marks = parseMarks(marksField.getText());
            manager.addStudent(new Student(id, name, marks));
            showInfo("Student added successfully.");
            clearInputFields();
            refreshTable();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleUpdateMarks() {
        try {
            int id = parseIntegerField(idField, "ID");
            List<Integer> marks = parseMarks(marksField.getText());
            manager.updateStudentMarks(id, marks);
            showInfo("Marks updated successfully.");
            clearInputFields();
            refreshTable();
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleSearchById() {
        try {
            int id = parseIntegerField(searchField, "Search ID");
            manager.findStudentById(id).ifPresentOrElse(
                    student -> showInfo(student.toString()),
                    () -> showError("Student with ID " + id + " not found."));
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private void handleShowToppers() {
        List<Student> toppers = manager.getToppers();
        if (toppers.isEmpty()) {
            showInfo("No topper available.");
            return;
        }

        String message = toppers.stream()
                .map(Student::toString)
                .collect(Collectors.joining("\n"));
        showInfo("Topper(s):\n" + message);
    }

    private void handleShowFailing() {
        List<Student> failingStudents = manager.getFailingStudents(60);
        if (failingStudents.isEmpty()) {
            showInfo("No failing students.");
            return;
        }

        String message = failingStudents.stream()
                .map(Student::toString)
                .collect(Collectors.joining("\n"));
        showInfo("Failing students:\n" + message);
    }

    private void handleRemoveStudent() {
        try {
            int id = parseIntegerField(idField, "ID");
            if (manager.removeStudentById(id)) {
                showInfo("Student removed successfully.");
                clearInputFields();
                refreshTable();
            } else {
                showError("Student with ID " + id + " not found.");
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private int parseIntegerField(TextField field, String label) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            throw new IllegalArgumentException(label + " cannot be empty.");
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " must be a valid integer.");
        }
    }

    private List<Integer> parseMarks(String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }

        return List.of(text.split(","))
                .stream()
                .map(String::trim)
                .filter(mark -> !mark.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private void refreshTable() {
        studentData.setAll(manager.getAllStudents());
    }

    private void clearInputFields() {
        idField.clear();
        nameField.clear();
        marksField.clear();
        searchField.clear();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
