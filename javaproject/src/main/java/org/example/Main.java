package org.example;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;
import java.util.stream.Collectors;

class Student {
    private String name;
    private int studentId;
    private String email;
    private Date dateOfBirth;

    public Student(String name, int studentId, String email, Date dateOfBirth) {
        this.name = name;
        this.studentId = studentId;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getEmail() {
        return email;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return "Name: " + name +
                ", Student ID: " + studentId +
                ", Email: " + email +
                ", Date of Birth: " + dateFormat.format(dateOfBirth);
    }
}

class StudentManager {
    private List<Student> studentList;

    public StudentManager() {
        studentList = new ArrayList<>();
    }

    public void addStudent(Student student) {
        studentList.add(student);
        Logger.getLogger(StudentManager.class.getName()).log(Level.INFO, "Student added: " + student.getName());
    }

    public void removeStudent(int studentId) {
        Optional<Student> studentToRemove = studentList.stream()
                .filter(student -> student.getStudentId() == studentId)
                .findFirst();

        if (studentToRemove.isPresent()) {
            studentList.remove(studentToRemove.get());
            Logger.getLogger(StudentManager.class.getName()).log(Level.INFO, "Student removed: " + studentToRemove.get().getName());
        } else {
            Logger.getLogger(StudentManager.class.getName()).log(Level.WARNING, "No student found with ID: " + studentId);
        }
    }

    public List<Student> searchStudents(String searchTerm) {
        return studentList.stream()
                .filter(student -> student.getName().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void generateReport() {
        for (Student student : studentList) {
            System.out.println(student);
        }
    }

    public void saveRecordsToFile(String filename) {
        try {
            FileWriter writer = new FileWriter(filename);
            for (Student student : studentList) {
                writer.write(student.getName() + "," +
                        student.getStudentId() + "," +
                        student.getEmail() + "," +
                        student.getDateOfBirth().getTime() + "\n");
            }
            writer.close();
            Logger.getLogger(StudentManager.class.getName()).log(Level.INFO, "Student records saved to file: " + filename);
        } catch (IOException e) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, "Error saving student records to file: " + filename, e);
        }
    }

    public void loadRecordsFromFile(String filename) {
        try {
            List<String> lines = FileUtils.readLines(new File(filename), "UTF-8");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

            for (String line : lines) {
                String[] parts = line.split(",");
                String name = parts[0];
                int studentId = Integer.parseInt(parts[1]);
                String email = parts[2];
                Date dateOfBirth = new Date(Long.parseLong(parts[3]));

                Student student = new Student(name, studentId, email, dateOfBirth);
                studentList.add(student);
            }

            Logger.getLogger(StudentManager.class.getName()).log(Level.INFO, "Student records loaded from file: " + filename);
        } catch (IOException e) {
            Logger.getLogger(StudentManager.class.getName()).log(Level.SEVERE, "Error loading student records from file: " + filename, e);
        }
    }
}

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager studentManager = new StudentManager();

    public static void main(String[] args) {
        configureLogger();

        boolean exit = false;

        while (!exit) {
            displayMenu();
            int choice = getChoice();

            switch (choice) {
                case 1:
                    addStudent();
                    break;
                case 2:
                    removeStudent();
                    break;
                case 3:
                    searchStudents();
                    break;
                case 4:
                    generateReport();
                    break;
                case 5:
                    saveRecordsToFile();
                    break;
                case 6:
                    loadRecordsFromFile();
                    break;
                case 7:
                    exit = true;
                    System.out.println("Exiting the application.");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void configureLogger() {
        Logger logger = Logger.getLogger(StudentManager.class.getName());
        logger.setLevel(Level.ALL);

        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.ALL);
        logger.addHandler(consoleHandler);

        try {
            FileHandler fileHandler = new FileHandler("application.log");
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error configuring logger.", e);
        }
    }

    private static void displayMenu() {
        System.out.println("\n***** Student Records Management *****");
        System.out.println("1. Add Student");
        System.out.println("2. Remove Student");
        System.out.println("3. Search Students");
        System.out.println("4. Generate Report");
        System.out.println("5. Save Records to File");
        System.out.println("6. Load Records from File");
        System.out.println("7. Exit");
        System.out.print("Enter your choice: ");
    }

    private static int getChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a valid choice.");
            scanner.next();
        }
        return scanner.nextInt();
    }

    private static void addStudent() {
        System.out.print("Enter student name: ");
        String name = scanner.next();

        int studentId = 0;
        boolean validInput = false;
        while (!validInput) {
            try {
                System.out.print("Enter student ID: ");
                studentId = scanner.nextInt();
                validInput = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
                scanner.nextLine(); // Clear the input buffer
            }
        }

        System.out.print("Enter student email: ");
        String email = scanner.next();

        System.out.print("Enter student date of birth (dd/MM/yyyy): ");
        String dateOfBirthStr = scanner.next();

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dateOfBirth = dateFormat.parse(dateOfBirthStr);

            Student student = new Student(name, studentId, email, dateOfBirth);
            studentManager.addStudent(student);
        } catch (Exception e) {
            System.out.println("Error adding student: " + e.getMessage());
        }
    }


    private static void removeStudent() {
        System.out.print("Enter student ID to remove: ");
        int studentId = scanner.nextInt();

        studentManager.removeStudent(studentId);
    }

    private static void searchStudents() {
        System.out.print("Enter search term: ");
        String searchTerm = scanner.next();

        List<Student> searchResults = studentManager.searchStudents(searchTerm);

        if (searchResults.isEmpty()) {
            System.out.println("No students found matching the search term.");
        } else {
            System.out.println("Search Results:");
            for (Student student : searchResults) {
                System.out.println(student);
            }
        }
    }

    private static void generateReport() {
        System.out.println("Student Records:");
        studentManager.generateReport();
    }

    private static void saveRecordsToFile() {
        System.out.print("Enter file name to save records: ");
        String filename = scanner.next();

        studentManager.saveRecordsToFile(filename);
    }

    private static void loadRecordsFromFile() {
        System.out.print("Enter file name to load records: ");
        String filename = scanner.next();

        studentManager.loadRecordsFromFile(filename);
    }
}
